from pathlib import Path

from fastapi.testclient import TestClient

from pharmacy_mock_api.app import MockSettings, create_app


def client() -> TestClient:
    return TestClient(create_app(MockSettings(latency_ms=0, scenario="seeded", cors_origins=["*"], static_dir=None)))


def login(test_client: TestClient, username: str = "super") -> dict[str, str]:
    response = test_client.post(
        "/api/um/v1/auth/login",
        json={"username": username, "password": "qa1234", "system": "PHARMACY"},
    )
    assert response.status_code == 200
    return {"Authorization": f"Bearer {response.json()['accessToken']}"}


def test_health_and_openapi() -> None:
    with client() as test_client:
        assert test_client.get("/health").json() == {
            "status": "ok",
            "service": "pharmacy-mock-api",
            "scenario": "seeded",
        }
        paths = test_client.get("/openapi.json").json()["paths"]
        assert "/api/um/v1/auth/login" in paths
        assert "/api/pharmacy/v1/drugs" in paths
        assert "/api/pharmacy/v1/report/dashboard" in paths


def test_login_exposes_each_qa_role() -> None:
    with client() as test_client:
        for username, role in (("super", "SUPER"), ("admin", "ADMIN"), ("manager", "MANAGER"), ("user", "USER")):
            response = test_client.get("/api/um/v1/user/info", headers=login(test_client, username))
            assert response.status_code == 200
            assert response.json()["username"] == username
            assert response.json()["role"] == role


def test_invalid_login_and_missing_token_are_rejected() -> None:
    with client() as test_client:
        response = test_client.post(
            "/api/um/v1/auth/login",
            json={"username": "super", "password": "wrong", "system": "PHARMACY"},
        )
        assert response.status_code == 401
        assert test_client.get("/api/pharmacy/v1/drugs").status_code == 401


def test_seeded_scenario_populates_post_login_screens() -> None:
    with client() as test_client:
        headers = login(test_client)
        assert len(test_client.get("/api/pharmacy/v1/drugs", headers=headers).json()) >= 6
        assert len(test_client.get("/api/pharmacy/v1/customers", headers=headers).json()) >= 4
        dashboard = test_client.get("/api/pharmacy/v1/report/dashboard", headers=headers).json()
        assert dashboard["summary"]["today_bills"] > 0
        assert dashboard["recent_sales"]
        movements = test_client.get("/api/pharmacy/v1/movements", headers=headers).json()
        assert movements["total"] == len(movements["items"])


def test_empty_scenario_can_be_selected_per_request() -> None:
    with client() as test_client:
        headers = {**login(test_client), "X-Mock-Scenario": "empty"}
        response = test_client.get("/api/pharmacy/v1/drugs", headers=headers)
        assert response.status_code == 200
        assert response.json() == []
        assert response.headers["X-Mock-Scenario"] == "empty"


def test_reset_restores_mutated_state() -> None:
    with client() as test_client:
        headers = login(test_client)
        created = test_client.post(
            "/api/pharmacy/v1/customers",
            headers=headers,
            json={"name": "Temporary QA customer", "phone": "", "price_tier": "A", "disease": ""},
        )
        assert created.status_code == 201
        assert len(test_client.get("/api/pharmacy/v1/customers", headers=headers).json()) == 5
        assert test_client.post("/__mock/reset").status_code == 200
        assert len(test_client.get("/api/pharmacy/v1/customers", headers=headers).json()) == 4


def test_optional_static_app_uses_the_same_origin(tmp_path: Path) -> None:
    (tmp_path / "index.html").write_text('<title>QA app</title><script src="app.js"></script>', encoding="utf-8")
    (tmp_path / "app.js").write_text("true", encoding="utf-8")
    settings = MockSettings(latency_ms=0, scenario="seeded", cors_origins=["*"], static_dir=tmp_path)
    with TestClient(create_app(settings)) as test_client:
        index_response = test_client.get("/")
        assert '<script src="app.js?qa=' in index_response.text
        assert index_response.headers["Cache-Control"] == "no-store"
        assert test_client.get("/health").json()["status"] == "ok"
