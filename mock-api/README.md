# Pharmacy Mock API

Standalone Python/FastAPI service for deterministic Pharmacy client development and post-login visual QA. It exposes the same host-level routes used by the KMP app:

- `/api/um/v1/*` for login, profile, and users
- `/api/pharmacy/v1/*` for drugs, stock, sales, reports, settings, KY forms, and purchasing
- `/docs` and `/openapi.json` for other projects to discover the contract

The service has no MongoDB, Redis, or external auth dependency. Mutations stay in memory and `POST /__mock/reset` restores the seed data.

## Run locally

```bash
cd mock-api
python3 -m venv .venv
.venv/bin/pip install -e '.[test]'
.venv/bin/pharmacy-mock-api
```

The default base URL is `http://localhost:8787`.

## QA accounts

All accounts use password `qa1234` and system `PHARMACY`.

| Username | Role |
|---|---|
| `super` | `SUPER` |
| `admin` | `ADMIN` |
| `manager` | `MANAGER` |
| `user` | `USER` |

```bash
curl -X POST http://localhost:8787/api/um/v1/auth/login \
  -H 'Content-Type: application/json' \
  -d '{"username":"super","password":"qa1234","system":"PHARMACY"}'
```

## Scenarios and latency

| Environment variable | Default | Meaning |
|---|---:|---|
| `MOCK_API_HOST` | `0.0.0.0` | Bind address |
| `MOCK_API_PORT` | `8787` | HTTP port |
| `MOCK_API_LATENCY_MS` | `80` | Artificial latency for every request |
| `MOCK_API_SCENARIO` | `seeded` | `seeded`, `empty`, or `error` |
| `MOCK_API_CORS_ORIGINS` | `*` | Comma-separated allowed origins |
| `MOCK_API_STATIC_DIR` | empty | Optional built web app directory served from `/` |

Clients can override the scenario for one request with `X-Mock-Scenario: empty` or `X-Mock-Scenario: error`.

## Same-origin web QA

The optional static mode serves a built web client and the mock routes from one origin. This avoids browser restrictions on cross-port localhost requests. The index response also adds a build-time cache token to JavaScript entrypoints so each rebuild is loaded immediately:

```bash
./gradlew :composeApp:wasmJsBrowserDevelopmentExecutableDistribution
MOCK_API_PORT=8088 \
MOCK_API_STATIC_DIR=composeApp/build/dist/wasmJs/developmentExecutable \
mock-api/.venv/bin/pharmacy-mock-api
```

Open `http://localhost:8088/?apiBaseUrl=http://localhost:8088`.

## Docker

```bash
docker build -t pharmacy-mock-api ./mock-api
docker run --rm -p 8787:8787 pharmacy-mock-api
```

Other applications can use the mock by setting their API base URL to the machine or container address, for example `http://localhost:8787` or `http://pharmacy-mock-api:8787` in Docker Compose.

## Verify

```bash
cd mock-api
.venv/bin/pytest
```
