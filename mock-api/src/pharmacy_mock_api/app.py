import asyncio
import os
import re
from copy import deepcopy
from dataclasses import dataclass
from pathlib import Path
from typing import Any, Annotated
from uuid import uuid4

from fastapi import Body, Depends, FastAPI, Header, HTTPException, Request, Response, status
from fastapi.middleware.cors import CORSMiddleware
from fastapi.responses import HTMLResponse, JSONResponse, PlainTextResponse
from fastapi.staticfiles import StaticFiles

from pharmacy_mock_api.fixtures import seed_state


PHARMACY_PREFIX = "/api/pharmacy/v1"
UM_PREFIX = "/api/um/v1"
QA_PASSWORD = "qa1234"


@dataclass(frozen=True)
class MockSettings:
    latency_ms: int
    scenario: str
    cors_origins: list[str]
    static_dir: Path | None = None

    @classmethod
    def from_env(cls) -> "MockSettings":
        origins = [value.strip() for value in os.getenv("MOCK_API_CORS_ORIGINS", "*").split(",") if value.strip()]
        static_dir_value = os.getenv("MOCK_API_STATIC_DIR", "").strip()
        return cls(
            latency_ms=max(0, int(os.getenv("MOCK_API_LATENCY_MS", "80"))),
            scenario=os.getenv("MOCK_API_SCENARIO", "seeded").lower(),
            cors_origins=origins or ["*"],
            static_dir=Path(static_dir_value).expanduser().resolve() if static_dir_value else None,
        )


class MockStore:
    def __init__(self) -> None:
        self.reset()

    def reset(self) -> None:
        state = seed_state()
        self.drugs: list[dict[str, Any]] = state["drugs"]
        self.customers: list[dict[str, Any]] = state["customers"]
        self.suppliers: list[dict[str, Any]] = state["suppliers"]
        self.users: list[dict[str, Any]] = state["users"]
        self.sales: list[dict[str, Any]] = state["sales"]
        self.sale_items: dict[str, list[dict[str, Any]]] = state["sale_items"]
        self.lots: list[dict[str, Any]] = state["lots"]
        self.purchase_orders: list[dict[str, Any]] = state["purchase_orders"]


def selected_scenario(request: Request) -> str:
    return request.headers.get("X-Mock-Scenario", request.app.state.settings.scenario).lower()


def collection(request: Request, values: list[dict[str, Any]]) -> list[dict[str, Any]]:
    return [] if selected_scenario(request) == "empty" else deepcopy(values)


def bearer_user(
    request: Request,
    authorization: Annotated[str | None, Header()] = None,
) -> dict[str, Any]:
    if not authorization or not authorization.startswith("Bearer mock-token-"):
        raise HTTPException(status_code=status.HTTP_401_UNAUTHORIZED, detail="Missing or invalid mock token")
    username = authorization.removeprefix("Bearer mock-token-")
    user = next((item for item in request.app.state.store.users if item["username"] == username), None)
    if user is None:
        raise HTTPException(status_code=status.HTTP_401_UNAUTHORIZED, detail="Unknown mock user")
    return user


CurrentUser = Annotated[dict[str, Any], Depends(bearer_user)]


def require_item(values: list[dict[str, Any]], item_id: str) -> dict[str, Any]:
    item = next((value for value in values if value["id"] == item_id), None)
    if item is None:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="Mock resource not found")
    return item


def next_id(prefix: str) -> str:
    return f"{prefix}-{uuid4().hex[:8]}"


def create_app(settings: MockSettings | None = None) -> FastAPI:
    config = settings or MockSettings.from_env()
    api = FastAPI(
        title="Pharmacy Mock API",
        version="0.1.0",
        description="Deterministic UM and Pharmacy API double for client development and visual QA",
    )
    api.state.settings = config
    api.state.store = MockStore()
    api.add_middleware(
        CORSMiddleware,
        allow_origins=config.cors_origins,
        allow_credentials=config.cors_origins != ["*"],
        allow_methods=["*"],
        allow_headers=["*"],
        expose_headers=["X-Mock-Scenario"],
    )

    @api.middleware("http")
    async def mock_controls(request: Request, call_next: Any) -> Response:
        scenario = selected_scenario(request)
        if config.latency_ms > 0:
            await asyncio.sleep(config.latency_ms / 1000)
        if scenario == "error" and request.url.path not in {"/health", "/__mock/reset", "/docs", "/openapi.json"}:
            return JSONResponse(status_code=503, content={"detail": "Forced mock failure"}, headers={"X-Mock-Scenario": scenario})
        response = await call_next(request)
        response.headers["X-Mock-Scenario"] = scenario
        return response

    @api.get("/health")
    def health() -> dict[str, Any]:
        return {"status": "ok", "service": "pharmacy-mock-api", "scenario": config.scenario}

    @api.post("/__mock/reset")
    def reset() -> dict[str, str]:
        api.state.store.reset()
        return {"status": "reset"}

    @api.post(f"{UM_PREFIX}/auth/login")
    def login(payload: Annotated[dict[str, Any], Body()]) -> dict[str, str]:
        username = str(payload.get("username", "")).strip().lower()
        password = str(payload.get("password", ""))
        system = str(payload.get("system", "")).upper()
        user = next((item for item in api.state.store.users if item["username"] == username), None)
        if user is None or password != QA_PASSWORD or system != "PHARMACY":
            raise HTTPException(status_code=status.HTTP_401_UNAUTHORIZED, detail="Use a mock username and qa1234")
        return {"accessToken": f"mock-token-{username}"}

    @api.get(f"{UM_PREFIX}/user/info")
    def user_info(user: CurrentUser) -> dict[str, Any]:
        return {
            "id": user["id"],
            "username": user["username"],
            "firstName": user["firstName"],
            "lastName": user["lastName"],
            "role": user["role"],
            "email": user["email"],
            "phone": user["phone"],
        }

    @api.post(f"{UM_PREFIX}/auth/logout", status_code=204)
    def logout(_: CurrentUser) -> Response:
        return Response(status_code=204)

    @api.get(f"{UM_PREFIX}/user")
    def users(request: Request, _: CurrentUser) -> list[dict[str, Any]]:
        return collection(request, api.state.store.users)

    @api.post(f"{UM_PREFIX}/user", status_code=201)
    def create_user(payload: Annotated[dict[str, Any], Body()], _: CurrentUser) -> dict[str, Any]:
        item = {
            "id": next_id("user"),
            "firstName": payload.get("firstName", ""),
            "lastName": payload.get("lastName", ""),
            "username": payload.get("username", "qa-user"),
            "clientId": "000",
            "role": payload.get("role", "USER"),
            "status": "ACTIVE",
            "phone": payload.get("phone", ""),
            "email": payload.get("email", ""),
            "createdDate": "2026-07-21T08:00:00Z",
            "updatedDate": "2026-07-21T08:00:00Z",
        }
        api.state.store.users.append(item)
        return deepcopy(item)

    @api.put(f"{UM_PREFIX}/user/info")
    def update_profile(payload: Annotated[dict[str, Any], Body()], user: CurrentUser) -> dict[str, Any]:
        user.update({key: payload[key] for key in ("firstName", "lastName", "phone", "email") if key in payload})
        return deepcopy(user)

    @api.put(f"{UM_PREFIX}/user/change-password", status_code=204)
    def change_password(_: Annotated[dict[str, Any], Body()], __: CurrentUser) -> Response:
        return Response(status_code=204)

    @api.put(f"{UM_PREFIX}/user/{{user_id}}")
    def update_user(user_id: str, payload: Annotated[dict[str, Any], Body()], _: CurrentUser) -> dict[str, Any]:
        item = require_item(api.state.store.users, user_id)
        item.update({key: payload[key] for key in ("firstName", "lastName", "phone", "email") if key in payload})
        return deepcopy(item)

    @api.patch(f"{UM_PREFIX}/user/{{user_id}}/{{action}}")
    def patch_user(user_id: str, action: str, payload: Annotated[dict[str, Any], Body()], _: CurrentUser) -> dict[str, Any]:
        item = require_item(api.state.store.users, user_id)
        if action == "role":
            item["role"] = payload.get("role", item["role"])
        elif action == "status":
            item["status"] = payload.get("status", item["status"])
        elif action == "set-password":
            return {}
        else:
            raise HTTPException(status_code=404, detail="Unknown mock user action")
        return deepcopy(item)

    @api.delete(f"{UM_PREFIX}/user/{{user_id}}", status_code=204)
    def delete_user(user_id: str, _: CurrentUser) -> Response:
        require_item(api.state.store.users, user_id)
        api.state.store.users = [item for item in api.state.store.users if item["id"] != user_id]
        return Response(status_code=204)

    @api.get(f"{PHARMACY_PREFIX}/drugs")
    def drugs(request: Request, _: CurrentUser) -> list[dict[str, Any]]:
        return collection(request, api.state.store.drugs)

    @api.get(f"{PHARMACY_PREFIX}/drugs/low-stock")
    def low_stock(request: Request, _: CurrentUser) -> list[dict[str, Any]]:
        values = [item for item in api.state.store.drugs if item["stock"] <= item["min_stock"]]
        return collection(request, values)

    @api.get(f"{PHARMACY_PREFIX}/drugs/reorder-suggestions")
    def reorder_suggestions(request: Request, _: CurrentUser) -> list[dict[str, Any]]:
        values = [
            {
                "drug_id": item["id"],
                "drug_name": item["name"],
                "unit": item["unit"],
                "current_stock": item["stock"],
                "min_stock": item["min_stock"],
                "qty_sold": 46,
                "avg_daily_sale": 3.3,
                "days_left": round(item["stock"] / 3.3, 1),
                "suggested_qty": max(item["min_stock"] * 2 - item["stock"], 0),
                "cost_price": item["cost_price"],
                "sell_price": item["sell_price"],
            }
            for item in api.state.store.drugs
            if item["stock"] <= item["min_stock"]
        ]
        return collection(request, values)

    @api.post(f"{PHARMACY_PREFIX}/drugs", status_code=201)
    def create_drug(payload: Annotated[dict[str, Any], Body()], _: CurrentUser) -> dict[str, Any]:
        item = {"id": next_id("drug"), **payload, "next_lot": None}
        api.state.store.drugs.append(item)
        return deepcopy(item)

    @api.put(f"{PHARMACY_PREFIX}/drugs/{{drug_id}}", status_code=204)
    def update_drug(drug_id: str, payload: Annotated[dict[str, Any], Body()], _: CurrentUser) -> Response:
        require_item(api.state.store.drugs, drug_id).update(payload)
        return Response(status_code=204)

    @api.post(f"{PHARMACY_PREFIX}/drugs/bulk")
    def bulk_drugs(payload: Annotated[dict[str, Any], Body()], _: CurrentUser) -> dict[str, Any]:
        return {"imported": len(payload.get("drugs", [])), "errors": []}

    @api.get(f"{PHARMACY_PREFIX}/customers")
    def customers(request: Request, _: CurrentUser) -> list[dict[str, Any]]:
        return collection(request, api.state.store.customers)

    @api.post(f"{PHARMACY_PREFIX}/customers", status_code=201)
    def create_customer(payload: Annotated[dict[str, Any], Body()], _: CurrentUser) -> dict[str, Any]:
        item = {"id": next_id("customer"), **payload}
        api.state.store.customers.append(item)
        return deepcopy(item)

    @api.put(f"{PHARMACY_PREFIX}/customers/{{customer_id}}", status_code=204)
    def update_customer(customer_id: str, payload: Annotated[dict[str, Any], Body()], _: CurrentUser) -> Response:
        require_item(api.state.store.customers, customer_id).update(payload)
        return Response(status_code=204)

    @api.get(f"{PHARMACY_PREFIX}/customers/{{customer_id}}/sales")
    def customer_sales(customer_id: str, request: Request, _: CurrentUser) -> list[dict[str, Any]]:
        customer = require_item(api.state.store.customers, customer_id)
        values = [sale for sale in api.state.store.sales if sale["customer_name"] == customer["name"]]
        return collection(request, values)

    @api.get(f"{PHARMACY_PREFIX}/suppliers")
    def suppliers(request: Request, _: CurrentUser) -> list[dict[str, Any]]:
        return collection(request, api.state.store.suppliers)

    @api.post(f"{PHARMACY_PREFIX}/suppliers", status_code=201)
    def create_supplier(payload: Annotated[dict[str, Any], Body()], _: CurrentUser) -> dict[str, Any]:
        item = {"id": next_id("supplier"), **payload}
        api.state.store.suppliers.append(item)
        return deepcopy(item)

    @api.put(f"{PHARMACY_PREFIX}/suppliers/{{supplier_id}}", status_code=204)
    def update_supplier(supplier_id: str, payload: Annotated[dict[str, Any], Body()], _: CurrentUser) -> Response:
        require_item(api.state.store.suppliers, supplier_id).update(payload)
        return Response(status_code=204)

    @api.delete(f"{PHARMACY_PREFIX}/suppliers/{{supplier_id}}", status_code=204)
    def delete_supplier(supplier_id: str, _: CurrentUser) -> Response:
        require_item(api.state.store.suppliers, supplier_id)
        api.state.store.suppliers = [item for item in api.state.store.suppliers if item["id"] != supplier_id]
        return Response(status_code=204)

    @api.get(f"{PHARMACY_PREFIX}/drugs/{{drug_id}}/lots")
    def drug_lots(drug_id: str, request: Request, _: CurrentUser) -> list[dict[str, Any]]:
        return collection(request, [item for item in api.state.store.lots if item["drug_id"] == drug_id])

    @api.post(f"{PHARMACY_PREFIX}/drugs/{{drug_id}}/lots", status_code=201)
    def create_lot(drug_id: str, payload: Annotated[dict[str, Any], Body()], _: CurrentUser) -> dict[str, Any]:
        drug = require_item(api.state.store.drugs, drug_id)
        item = {"id": next_id("lot"), "drug_id": drug_id, "drug_name": drug["name"], **payload, "import_date": payload.get("import_date") or "2026-07-21", "remaining": payload.get("quantity", 0)}
        api.state.store.lots.append(item)
        return deepcopy(item)

    @api.delete(f"{PHARMACY_PREFIX}/drugs/{{drug_id}}/lots/{{lot_id}}", status_code=204)
    def delete_lot(drug_id: str, lot_id: str, _: CurrentUser) -> Response:
        require_item([item for item in api.state.store.lots if item["drug_id"] == drug_id], lot_id)
        api.state.store.lots = [item for item in api.state.store.lots if item["id"] != lot_id]
        return Response(status_code=204)

    @api.get(f"{PHARMACY_PREFIX}/drugs/{{drug_id}}/adjustments")
    def adjustments(drug_id: str, request: Request, _: CurrentUser) -> list[dict[str, Any]]:
        drug = require_item(api.state.store.drugs, drug_id)
        values = [{"id": "adjustment-001", "drug_id": drug_id, "drug_name": drug["name"], "delta": -2, "before": drug["stock"] + 2, "after": drug["stock"], "reason": "damaged", "note": "บรรจุภัณฑ์เสียหาย", "created_at": "2026-07-19T03:30:00Z"}]
        return collection(request, values)

    @api.post(f"{PHARMACY_PREFIX}/drugs/{{drug_id}}/adjustments", status_code=204)
    def create_adjustment(drug_id: str, payload: Annotated[dict[str, Any], Body()], _: CurrentUser) -> Response:
        drug = require_item(api.state.store.drugs, drug_id)
        drug["stock"] += int(payload.get("delta", 0))
        return Response(status_code=204)

    @api.get(f"{PHARMACY_PREFIX}/lots/expiring")
    def expiring_lots(request: Request, _: CurrentUser) -> list[dict[str, Any]]:
        values = [
            {"id": item["id"], "drug_id": item["drug_id"], "drug_name": item["drug_name"], "lot_number": item["lot_number"], "expiry_date": item["expiry_date"], "remaining": item["remaining"], "days_left": 25 if item["id"] == "lot-005" else 56}
            for item in api.state.store.lots
            if item["id"] in {"lot-002", "lot-005"}
        ]
        return collection(request, values)

    @api.post(f"{PHARMACY_PREFIX}/lots/writeoff")
    def writeoff_lots(payload: Annotated[dict[str, Any], Body()], _: CurrentUser) -> dict[str, Any]:
        lot_ids = set(payload.get("lot_ids", []))
        found = {item["id"] for item in api.state.store.lots if item["id"] in lot_ids}
        api.state.store.lots = [item for item in api.state.store.lots if item["id"] not in found]
        return {"written_off": len(found), "failed": [{"lot_id": item_id, "error": "not found"} for item_id in lot_ids - found]}

    @api.get(f"{PHARMACY_PREFIX}/stock-counts")
    def stock_counts(request: Request, _: CurrentUser) -> list[dict[str, Any]]:
        values = [{"id": "count-001", "count_no": "SC-202607-001", "note": "ตรวจนับประจำเดือน", "items": [{"drug_id": "drug-001", "drug_name": "พาราเซตามอล 500 มก.", "unit": "แผง", "system_stock": 126, "counted": 124, "delta": -2}, {"drug_id": "drug-002", "drug_name": "อะม็อกซีซิลลิน 500 มก.", "unit": "แผง", "system_stock": 8, "counted": 8, "delta": 0}], "created_at": "2026-07-20T10:00:00Z"}]
        return collection(request, values)

    @api.post(f"{PHARMACY_PREFIX}/stock-counts", status_code=201)
    def create_stock_count(payload: Annotated[dict[str, Any], Body()], _: CurrentUser) -> dict[str, Any]:
        return {"id": next_id("count"), "count_no": "SC-MOCK-NEW", "note": payload.get("note", ""), "items": payload.get("items", []), "created_at": "2026-07-21T08:00:00Z"}

    @api.get(f"{PHARMACY_PREFIX}/imports")
    def imports(request: Request, _: CurrentUser) -> list[dict[str, Any]]:
        values = [{key: value for key, value in item.items() if key != "items"} for item in api.state.store.purchase_orders]
        return collection(request, values)

    @api.get(f"{PHARMACY_PREFIX}/imports/{{order_id}}")
    def import_detail(order_id: str, _: CurrentUser) -> dict[str, Any]:
        return deepcopy(require_item(api.state.store.purchase_orders, order_id))

    @api.post(f"{PHARMACY_PREFIX}/imports", status_code=201)
    def create_import(payload: Annotated[dict[str, Any], Body()], _: CurrentUser) -> dict[str, Any]:
        item = {"id": next_id("import"), "doc_no": "PO-MOCK-NEW", **payload, "item_count": len(payload.get("items", [])), "total_cost": sum(value.get("qty", 0) * value.get("cost_price", 0) for value in payload.get("items", [])), "status": "draft", "created_at": "2026-07-21T08:00:00Z", "confirmed_at": None}
        api.state.store.purchase_orders.append(item)
        return deepcopy(item)

    @api.put(f"{PHARMACY_PREFIX}/imports/{{order_id}}")
    def update_import(order_id: str, payload: Annotated[dict[str, Any], Body()], _: CurrentUser) -> dict[str, Any]:
        item = require_item(api.state.store.purchase_orders, order_id)
        item.update(payload)
        item["item_count"] = len(item.get("items", []))
        item["total_cost"] = sum(value.get("qty", 0) * value.get("cost_price", 0) for value in item.get("items", []))
        return deepcopy(item)

    @api.post(f"{PHARMACY_PREFIX}/imports/{{order_id}}/confirm")
    def confirm_import(order_id: str, _: CurrentUser) -> dict[str, Any]:
        item = require_item(api.state.store.purchase_orders, order_id)
        item.update({"status": "confirmed", "confirmed_at": "2026-07-21T08:00:00Z"})
        return deepcopy(item)

    @api.delete(f"{PHARMACY_PREFIX}/imports/{{order_id}}", status_code=204)
    def delete_import(order_id: str, _: CurrentUser) -> Response:
        require_item(api.state.store.purchase_orders, order_id)
        api.state.store.purchase_orders = [item for item in api.state.store.purchase_orders if item["id"] != order_id]
        return Response(status_code=204)

    @api.get(f"{PHARMACY_PREFIX}/sales")
    def sales(request: Request, _: CurrentUser) -> list[dict[str, Any]]:
        return collection(request, api.state.store.sales)

    @api.post(f"{PHARMACY_PREFIX}/sales", status_code=201)
    def checkout(payload: Annotated[dict[str, Any], Body()], _: CurrentUser) -> dict[str, Any]:
        total = sum(float(item.get("price", 0)) * int(item.get("qty", 0)) - float(item.get("item_discount", 0)) for item in payload.get("items", [])) - float(payload.get("discount", 0))
        sale_id = next_id("sale")
        api.state.store.sales.insert(0, {"id": sale_id, "bill_no": "INV-MOCK-NEW", "customer_name": "ลูกค้าทั่วไป", "total": total, "discount": payload.get("discount", 0), "sold_at": "2026-07-21T08:00:00Z", "voided": False})
        return {"id": sale_id, "bill_no": "INV-MOCK-NEW", "total": total, "change": max(float(payload.get("received", 0)) - total, 0), "discount": payload.get("discount", 0), "stock_updates": [], "ky_skipped_by_cashier": payload.get("ky_skipped_by_cashier", False)}

    @api.get(f"{PHARMACY_PREFIX}/sales/{{sale_id}}/items")
    def sale_items(sale_id: str, request: Request, _: CurrentUser) -> list[dict[str, Any]]:
        require_item(api.state.store.sales, sale_id)
        return collection(request, api.state.store.sale_items.get(sale_id, []))

    @api.get(f"{PHARMACY_PREFIX}/sales/{{sale_id}}/returns")
    def sale_returns(sale_id: str, request: Request, _: CurrentUser) -> list[dict[str, Any]]:
        require_item(api.state.store.sales, sale_id)
        values = [{"id": "return-001", "return_no": "RET-202607-001", "sale_id": sale_id, "items": [{"sale_item_id": "sale-item-001", "qty": 1}], "refund": 25.0, "reason": "แพ้ยา"}] if sale_id == "sale-001" else []
        return collection(request, values)

    @api.post(f"{PHARMACY_PREFIX}/sales/{{sale_id}}/return", status_code=204)
    def create_return(sale_id: str, _: Annotated[dict[str, Any], Body()], __: CurrentUser) -> Response:
        require_item(api.state.store.sales, sale_id)
        return Response(status_code=204)

    @api.post(f"{PHARMACY_PREFIX}/sales/{{sale_id}}/void", status_code=204)
    def void_sale(sale_id: str, _: Annotated[dict[str, Any], Body()], __: CurrentUser) -> Response:
        require_item(api.state.store.sales, sale_id)["voided"] = True
        return Response(status_code=204)

    @api.get(f"{PHARMACY_PREFIX}/movements")
    def movements(request: Request, _: CurrentUser) -> dict[str, Any]:
        values = [
            {"id": "movement-001", "type": "SALE", "drug_id": "drug-001", "drug_name": "พาราเซตามอล 500 มก.", "delta": -2, "reference": "INV-20260721-001", "note": "", "at": "2026-07-21T03:15:00Z"},
            {"id": "movement-002", "type": "ADJUSTMENT", "drug_id": "drug-002", "drug_name": "อะม็อกซีซิลลิน 500 มก.", "delta": -2, "reference": "ADJ-001", "note": "บรรจุภัณฑ์เสียหาย", "at": "2026-07-19T03:30:00Z"},
            {"id": "movement-003", "type": "IMPORT", "drug_id": "drug-006", "drug_name": "น้ำเกลือล้างแผล โซเดียมคลอไรด์ 0.9%", "delta": 50, "reference": "PO-202607-001", "note": "รับสินค้า", "at": "2026-07-18T02:30:00Z"},
        ]
        items = collection(request, values)
        return {"total": len(items), "items": items}

    @api.get(f"{PHARMACY_PREFIX}/settings")
    def settings(_: CurrentUser) -> dict[str, Any]:
        return {"store": {"name": "ร้านยาเดฟเปอร์ สาขาทดสอบ Responsive", "address": "99/9 ถนนสุขุมวิท แขวงพระโขนง เขตคลองเตย กรุงเทพมหานคร 10110", "phone": "021234567", "tax_id": "0105559999999"}, "receipt": {"header": "ยินดีต้อนรับ", "footer": "ขอบคุณที่ใช้บริการ", "paper_width": "80", "show_pharmacist": True}, "stock": {"low_stock_threshold": 10, "reorder_days": 30, "reorder_lookahead": 14, "expiring_days": 60}, "pharmacist": {"name": "ภก. ทดสอบ ระบบ", "license_no": "ภ.12345"}, "ky": {"skip_auto": False, "default_buyer_address": "กรุงเทพมหานคร"}, "timezone": "Asia/Bangkok"}

    @api.put(f"{PHARMACY_PREFIX}/settings")
    def update_settings(payload: Annotated[dict[str, Any], Body()], _: CurrentUser) -> dict[str, Any]:
        return payload

    @api.get(f"{PHARMACY_PREFIX}/report/dashboard")
    def dashboard(request: Request, _: CurrentUser) -> dict[str, Any]:
        if selected_scenario(request) == "empty":
            return {"summary": {}, "daily": [], "monthly": [], "recent_sales": []}
        return {"summary": {"today_sales": 12840.0, "today_bills": 47, "month_sales": 284650.0, "stock_value": 438920.0, "low_stock": 3, "out_stock": 1}, "daily": [{"day": f"2026-07-{day:02d}", "total": total} for day, total in [(15, 8900.0), (16, 12450.0), (17, 10820.0), (18, 15600.0), (19, 9300.0), (20, 14200.0), (21, 12840.0)]], "monthly": [{"month": f"2026-{month:02d}", "revenue": 210000.0 + month * 9200, "cost": 118000.0 + month * 5100, "profit": 92000.0 + month * 4100} for month in range(2, 8)], "recent_sales": deepcopy(api.state.store.sales)}

    @api.get(f"{PHARMACY_PREFIX}/report/top-drugs")
    def top_drugs(request: Request, _: CurrentUser) -> list[dict[str, Any]]:
        values = [{"drug_id": item["id"], "drug_name": item["name"], "qty_sold": 92 - index * 11, "revenue": (92 - index * 11) * item["sell_price"]} for index, item in enumerate(api.state.store.drugs[:5])]
        return collection(request, values)

    @api.get(f"{PHARMACY_PREFIX}/report/slow-drugs")
    def slow_drugs(request: Request, _: CurrentUser) -> list[dict[str, Any]]:
        values = [{"drug_id": item["id"], "drug_name": item["name"], "stock": item["stock"], "unit": item["unit"]} for item in reversed(api.state.store.drugs[:4])]
        return collection(request, values)

    @api.get(f"{PHARMACY_PREFIX}/report/profit")
    def profit(request: Request, _: CurrentUser) -> dict[str, Any]:
        by_drug = [{"drug_id": item["id"], "drug_name": item["name"], "qty_sold": 35, "revenue": item["sell_price"] * 35, "cost": item["cost_price"] * 35, "profit": (item["sell_price"] - item["cost_price"]) * 35, "margin": 42.5} for item in api.state.store.drugs[:4]]
        return {"summary": {"revenue": 284650.0, "cost": 159400.0, "profit": 125250.0, "margin": 44.0, "bills": 923}, "by_drug": [] if selected_scenario(request) == "empty" else by_drug}

    @api.get(f"{PHARMACY_PREFIX}/report/eod")
    def eod(request: Request, _: CurrentUser) -> dict[str, Any]:
        bills = collection(request, api.state.store.sales)
        return {"date": request.query_params.get("date", "2026-07-21"), "bill_count": len(bills), "total_sales": sum(item["total"] for item in bills), "total_discount": sum(item["discount"] for item in bills), "total_received": 15000.0, "total_change": 1820.0, "net_cash": 13180.0, "bills": bills}

    @api.post(f"{PHARMACY_PREFIX}/report/eod/close")
    def close_eod(payload: Annotated[dict[str, Any], Body()], _: CurrentUser) -> dict[str, Any]:
        report = {"date": payload.get("date", "2026-07-21"), "bill_count": len(api.state.store.sales), "total_sales": sum(item["total"] for item in api.state.store.sales), "total_discount": sum(item["discount"] for item in api.state.store.sales), "total_received": 15000.0, "total_change": 1820.0, "net_cash": 13180.0, "bills": deepcopy(api.state.store.sales)}
        return {"close_id": next_id("close"), "date": report["date"], "closed_at": "2026-07-21T10:00:00Z", "closed_by": "super", "report": report}

    @api.get(f"{PHARMACY_PREFIX}/ky9")
    def ky9(request: Request, _: CurrentUser) -> list[dict[str, Any]]:
        values = [{"id": "ky9-001", "sale_id": "sale-001", "date": "2026-07-21", "drug_name": "พาราเซตามอล 500 มก.", "reg_no": "1A 123/67", "unit": "แผง", "qty": 20, "price_per_unit": 12.0, "total_value": 240.0, "seller": "บริษัท เมดิคอลซัพพลายประเทศไทย จำกัด", "invoice_no": "MS-78451", "created_at": "2026-07-21T03:15:00Z"}]
        return collection(request, values)

    @api.get(f"{PHARMACY_PREFIX}/ky10")
    def ky10(request: Request, _: CurrentUser) -> list[dict[str, Any]]:
        values = [{"id": "ky10-001", "sale_id": "sale-001", "date": "2026-07-21", "drug_name": "อะม็อกซีซิลลิน 500 มก.", "reg_no": "1A 456/67", "qty": 1, "unit": "แผง", "buyer_name": "สมชาย ใจดี", "buyer_address": "กรุงเทพมหานคร", "rx_no": "RX-10001", "doctor": "นพ. ทดสอบ ระบบ", "balance": 7, "created_at": "2026-07-21T03:15:00Z"}]
        return collection(request, values)

    @api.get(f"{PHARMACY_PREFIX}/ky11")
    def ky11(request: Request, _: CurrentUser) -> list[dict[str, Any]]:
        values = [{"id": "ky11-001", "sale_id": "sale-003", "date": "2026-07-20", "drug_name": "ไอบูโพรเฟน 400 มก.", "reg_no": "1A 998/65", "qty": 2, "unit": "แผง", "buyer_name": "อรทัย สุขใจ", "purpose": "ใช้ตามคำแนะนำเภสัชกร", "pharmacist": "ภก. ทดสอบ ระบบ", "created_at": "2026-07-20T09:05:00Z"}]
        return collection(request, values)

    @api.get(f"{PHARMACY_PREFIX}/ky12")
    def ky12(request: Request, _: CurrentUser) -> list[dict[str, Any]]:
        values = [{"id": "ky12-001", "sale_id": "sale-003", "date": "2026-07-20", "rx_no": "RX-10002", "patient_name": "อรทัย สุขใจ", "doctor": "พญ. ตัวอย่าง แพทย์", "hospital": "โรงพยาบาลตัวอย่าง", "drug_name": "ยาควบคุมตัวอย่าง", "qty": 1, "unit": "กล่อง", "total_value": 450.0, "status": "dispensed", "created_at": "2026-07-20T09:05:00Z"}]
        return collection(request, values)

    @api.post(f"{PHARMACY_PREFIX}/ky{{form_number}}", status_code=204)
    def create_ky(form_number: int, _: Annotated[dict[str, Any], Body()], __: CurrentUser) -> Response:
        if form_number not in {9, 10, 11, 12}:
            raise HTTPException(status_code=404, detail="Unknown KY form")
        return Response(status_code=204)

    @api.get(f"{PHARMACY_PREFIX}/export/{{form_name}}")
    def export_form(form_name: str, _: CurrentUser) -> PlainTextResponse:
        return PlainTextResponse(f"Mock export for {form_name}\n", media_type="text/csv", headers={"Content-Disposition": f'attachment; filename="{form_name}-mock.csv"'})

    @api.post(f"{PHARMACY_PREFIX}/labels/print")
    def print_labels(_: Annotated[dict[str, Any], Body()], __: CurrentUser) -> Response:
        return Response(content=b"%PDF-1.4\n% Pharmacy mock label\n", media_type="application/pdf")

    if config.static_dir is not None:
        index_path = config.static_dir / "index.html"

        @api.get("/", include_in_schema=False)
        def qa_web_index() -> HTMLResponse:
            build_token = max(
                (path.stat().st_mtime_ns for path in config.static_dir.iterdir() if path.is_file()),
                default=index_path.stat().st_mtime_ns,
            )
            html = index_path.read_text(encoding="utf-8")
            versioned_html = re.sub(
                r'(<script\b[^>]*\bsrc=["\'])([^"\']+\.js)(["\'])',
                rf"\g<1>\g<2>?qa={build_token}\g<3>",
                html,
            )
            return HTMLResponse(versioned_html, headers={"Cache-Control": "no-store"})

        api.mount("/", StaticFiles(directory=config.static_dir, html=True), name="qa-web")

    return api


app = create_app()
