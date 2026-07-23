from copy import deepcopy
from typing import Any


DRUGS = [
    {
        "id": "drug-001",
        "name": "พาราเซตามอล 500 มก.",
        "generic_name": "Paracetamol",
        "type": "ยาเม็ด",
        "strength": "500 mg",
        "barcode": "885000000001",
        "sell_price": 25.0,
        "cost_price": 12.0,
        "stock": 124,
        "min_stock": 30,
        "unit": "แผง",
        "reg_no": "1A 123/67",
        "prices": {"A": 25.0, "B": 23.0, "C": 21.0},
        "alt_units": [{"name": "กล่อง", "factor": 10, "sell_price": 220.0, "barcode": "885000000101"}],
        "report_types": [],
        "next_lot": {"lot_id": "lot-001", "lot_number": "PCM2601", "expiry_date": "2027-01-31"},
    },
    {
        "id": "drug-002",
        "name": "อะม็อกซีซิลลิน 500 มก.",
        "generic_name": "Amoxicillin",
        "type": "แคปซูล",
        "strength": "500 mg",
        "barcode": "885000000002",
        "sell_price": 85.0,
        "cost_price": 48.0,
        "stock": 8,
        "min_stock": 20,
        "unit": "แผง",
        "reg_no": "1A 456/67",
        "prices": {"A": 85.0, "B": 80.0},
        "alt_units": [],
        "report_types": ["KY10"],
        "next_lot": {"lot_id": "lot-002", "lot_number": "AMX2602", "expiry_date": "2026-09-15"},
    },
    {
        "id": "drug-003",
        "name": "ยาแก้ไอผสมมะขามป้อม",
        "generic_name": "Cough mixture",
        "type": "ยาน้ำ",
        "strength": "60 ml",
        "barcode": "885000000003",
        "sell_price": 55.0,
        "cost_price": 31.0,
        "stock": 0,
        "min_stock": 12,
        "unit": "ขวด",
        "reg_no": "G 789/66",
        "prices": {"A": 55.0},
        "alt_units": [],
        "report_types": [],
        "next_lot": None,
    },
    {
        "id": "drug-004",
        "name": "เซทิริซีน 10 มก.",
        "generic_name": "Cetirizine",
        "type": "ยาเม็ด",
        "strength": "10 mg",
        "barcode": "885000000004",
        "sell_price": 35.0,
        "cost_price": 16.5,
        "stock": 42,
        "min_stock": 15,
        "unit": "แผง",
        "reg_no": "1A 112/66",
        "prices": {"A": 35.0, "B": 32.0},
        "alt_units": [],
        "report_types": [],
        "next_lot": {"lot_id": "lot-004", "lot_number": "CTZ2603", "expiry_date": "2027-06-30"},
    },
    {
        "id": "drug-005",
        "name": "ไอบูโพรเฟน 400 มก.",
        "generic_name": "Ibuprofen",
        "type": "ยาเม็ด",
        "strength": "400 mg",
        "barcode": "885000000005",
        "sell_price": 45.0,
        "cost_price": 22.0,
        "stock": 17,
        "min_stock": 20,
        "unit": "แผง",
        "reg_no": "1A 998/65",
        "prices": {"A": 45.0},
        "alt_units": [],
        "report_types": ["KY11"],
        "next_lot": {"lot_id": "lot-005", "lot_number": "IBU2601", "expiry_date": "2026-08-20"},
    },
    {
        "id": "drug-006",
        "name": "น้ำเกลือล้างแผล โซเดียมคลอไรด์ 0.9%",
        "generic_name": "Sodium chloride",
        "type": "เวชภัณฑ์",
        "strength": "1000 ml",
        "barcode": "885000000006",
        "sell_price": 65.0,
        "cost_price": 38.0,
        "stock": 64,
        "min_stock": 10,
        "unit": "ขวด",
        "reg_no": "",
        "prices": {"A": 65.0, "B": 60.0},
        "alt_units": [],
        "report_types": [],
        "next_lot": {"lot_id": "lot-006", "lot_number": "NSS2604", "expiry_date": "2028-04-30"},
    },
]

CUSTOMERS = [
    {"id": "customer-001", "name": "สมชาย ใจดี", "phone": "0812345678", "price_tier": "A", "disease": "ความดันโลหิตสูง"},
    {"id": "customer-002", "name": "อรทัย สุขใจ", "phone": "0891234567", "price_tier": "B", "disease": "แพ้เพนิซิลลิน"},
    {"id": "customer-003", "name": "บริษัท คลินิกชุมชนอบอุ่น จำกัด", "phone": "021234567", "price_tier": "C", "disease": ""},
    {"id": "customer-004", "name": "วิชัย แสงทอง", "phone": "0865550199", "price_tier": "A", "disease": "เบาหวาน"},
]

SUPPLIERS = [
    {"id": "supplier-001", "name": "บริษัท เมดิคอลซัพพลายประเทศไทย จำกัด", "contact_name": "คุณกมล", "phone": "021112222", "address": "กรุงเทพมหานคร", "tax_id": "0105559000001", "notes": "ส่งของทุกวันจันทร์"},
    {"id": "supplier-002", "name": "ไทยฟาร์มา ดิสทริบิวชั่น", "contact_name": "คุณนที", "phone": "023334444", "address": "นนทบุรี", "tax_id": "0105559000002", "notes": ""},
    {"id": "supplier-003", "name": "เฮลท์แคร์โฮลเซล", "contact_name": "คุณพิม", "phone": "025556666", "address": "สมุทรปราการ", "tax_id": "0105559000003", "notes": "เครดิต 30 วัน"},
]

USERS = [
    {"id": "user-super", "firstName": "ผู้ดูแล", "lastName": "ระบบ QA", "username": "super", "clientId": "000", "role": "SUPER", "status": "ACTIVE", "phone": "0800000001", "email": "super@example.test", "createdDate": "2026-01-01T02:00:00Z", "updatedDate": "2026-07-01T02:00:00Z"},
    {"id": "user-admin", "firstName": "แอดมิน", "lastName": "ร้านยา", "username": "admin", "clientId": "000", "role": "ADMIN", "status": "ACTIVE", "phone": "0800000002", "email": "admin@example.test", "createdDate": "2026-01-02T02:00:00Z", "updatedDate": "2026-07-02T02:00:00Z"},
    {"id": "user-manager", "firstName": "ผู้จัดการ", "lastName": "สาขา", "username": "manager", "clientId": "000", "role": "MANAGER", "status": "ACTIVE", "phone": "0800000003", "email": "manager@example.test", "createdDate": "2026-01-03T02:00:00Z", "updatedDate": "2026-07-03T02:00:00Z"},
    {"id": "user-user", "firstName": "เภสัชกร", "lastName": "เวรเช้า", "username": "user", "clientId": "000", "role": "USER", "status": "ACTIVE", "phone": "0800000004", "email": "user@example.test", "createdDate": "2026-01-04T02:00:00Z", "updatedDate": "2026-07-04T02:00:00Z"},
]

SALES = [
    {"id": "sale-001", "bill_no": "INV-20260721-001", "customer_name": "สมชาย ใจดี", "total": 195.0, "discount": 10.0, "sold_at": "2026-07-21T03:15:00Z", "voided": False},
    {"id": "sale-002", "bill_no": "INV-20260721-002", "customer_name": "ลูกค้าทั่วไป", "total": 110.0, "discount": 0.0, "sold_at": "2026-07-21T04:32:00Z", "voided": False},
    {"id": "sale-003", "bill_no": "INV-20260720-018", "customer_name": "อรทัย สุขใจ", "total": 340.0, "discount": 20.0, "sold_at": "2026-07-20T09:05:00Z", "voided": False},
    {"id": "sale-004", "bill_no": "INV-20260720-017", "customer_name": "วิชัย แสงทอง", "total": 65.0, "discount": 0.0, "sold_at": "2026-07-20T08:40:00Z", "voided": True},
]

SALE_ITEMS = {
    "sale-001": [
        {"id": "sale-item-001", "drug_id": "drug-001", "drug_name": "พาราเซตามอล 500 มก.", "qty": 2, "price": 25.0, "original_price": 25.0, "item_discount": 0.0, "unit": "แผง", "unit_factor": 1, "price_tier": "A", "lot_splits": [{"lot_id": "lot-001", "lot_number": "PCM2601", "qty": 2}]},
        {"id": "sale-item-002", "drug_id": "drug-002", "drug_name": "อะม็อกซีซิลลิน 500 มก.", "qty": 1, "price": 85.0, "original_price": 85.0, "item_discount": 0.0, "unit": "แผง", "unit_factor": 1, "price_tier": "A", "lot_splits": [{"lot_id": "lot-002", "lot_number": "AMX2602", "qty": 1}]},
        {"id": "sale-item-003", "drug_id": "drug-006", "drug_name": "น้ำเกลือล้างแผล โซเดียมคลอไรด์ 0.9%", "qty": 1, "price": 65.0, "original_price": 65.0, "item_discount": 5.0, "unit": "ขวด", "unit_factor": 1, "price_tier": "A", "lot_splits": [{"lot_id": "lot-006", "lot_number": "NSS2604", "qty": 1}]},
    ]
}

LOTS = [
    {"id": "lot-001", "drug_id": "drug-001", "drug_name": "พาราเซตามอล 500 มก.", "lot_number": "PCM2601", "expiry_date": "2027-01-31", "import_date": "2026-01-12", "cost_price": 12.0, "sell_price": 25.0, "quantity": 200, "remaining": 124},
    {"id": "lot-002", "drug_id": "drug-002", "drug_name": "อะม็อกซีซิลลิน 500 มก.", "lot_number": "AMX2602", "expiry_date": "2026-09-15", "import_date": "2026-02-10", "cost_price": 48.0, "sell_price": 85.0, "quantity": 100, "remaining": 8},
    {"id": "lot-005", "drug_id": "drug-005", "drug_name": "ไอบูโพรเฟน 400 มก.", "lot_number": "IBU2601", "expiry_date": "2026-08-20", "import_date": "2026-01-20", "cost_price": 22.0, "sell_price": 45.0, "quantity": 80, "remaining": 17},
]

PURCHASE_ORDERS = [
    {"id": "import-001", "doc_no": "PO-202607-001", "supplier": "บริษัท เมดิคอลซัพพลายประเทศไทย จำกัด", "invoice_no": "MS-78451", "receive_date": "2026-07-18", "items": [{"drug_id": "drug-001", "drug_name": "พาราเซตามอล 500 มก.", "lot_number": "PCM2701", "expiry_date": "2028-01-31", "qty": 100, "cost_price": 12.0, "sell_price": 25.0}], "item_count": 1, "total_cost": 1200.0, "status": "confirmed", "notes": "รับของครบ", "created_at": "2026-07-18T02:00:00Z", "confirmed_at": "2026-07-18T02:30:00Z"},
    {"id": "import-002", "doc_no": "PO-202607-002", "supplier": "ไทยฟาร์มา ดิสทริบิวชั่น", "invoice_no": "TF-99120", "receive_date": "2026-07-21", "items": [{"drug_id": "drug-002", "drug_name": "อะม็อกซีซิลลิน 500 มก.", "lot_number": "AMX2701", "expiry_date": "2028-03-31", "qty": 120, "cost_price": 48.0, "sell_price": 85.0}], "item_count": 1, "total_cost": 5760.0, "status": "draft", "notes": "รอตรวจจำนวน", "created_at": "2026-07-21T01:15:00Z", "confirmed_at": None},
]


def seed_state() -> dict[str, Any]:
    return deepcopy(
        {
            "drugs": DRUGS,
            "customers": CUSTOMERS,
            "suppliers": SUPPLIERS,
            "users": USERS,
            "sales": SALES,
            "sale_items": SALE_ITEMS,
            "lots": LOTS,
            "purchase_orders": PURCHASE_ORDERS,
        }
    )
