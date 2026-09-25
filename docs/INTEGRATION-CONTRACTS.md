# Cross-repository contracts

This map links the KMP client to the APIs that own its data. It separates observed behavior from the agreed target, so a missing endpoint or policy is not mistaken for an implemented feature.

| Boundary | Current code | Agreed target |
| --- | --- | --- |
| Login and users | KMP [AuthApi](../core/data/src/commonMain/kotlin/app/devper/pharm/data/remote/api/AuthApi.kt) and [UsersApi](../core/data/src/commonMain/kotlin/app/devper/pharm/data/remote/api/UsersApi.kt) call `um-api` `/api/um/v1`. [UM OpenAPI](https://github.com/app-devper/um-api/blob/develop/docs/openapi.yaml) owns that endpoint contract. | UM owns accounts, sessions, and role assignment. KMP and the pharmacy API enforce the same operation permissions. |
| Pharmacy authorization | The [pharmacy routes](https://github.com/app-devper/pharmacy-api/blob/develop/routes/routes.go) have USER and ADMIN groups. The [pharmacy auth middleware](https://github.com/app-devper/pharmacy-api/blob/develop/middleware/auth.go) trusts signed JWT role claims until token expiry; UM reloads current session and user state on its own requests. | UM exposes dedicated session/role verification; pharmacy caches by session ID for at most 30 seconds, binds the result to the signed token's user, system, and tenant/client ID, and stops confirming writes or sensitive reads when current permission cannot be established. MANAGER receives the agreed operational permissions; SUPER does not bypass tenant scope. |
| Checkout and stock | KMP [SaleApi](../core/data/src/commonMain/kotlin/app/devper/pharm/data/remote/api/SaleApi.kt) sends checkout to pharmacy API. The backend owns the confirmed sale and stock update in one transaction. | Preserve the cashier-approved price and submission time through offline replay; confirm the sale only after backend acceptance. Late confirmation adjusts a closed period with an audit trail. |
| Returns | KMP [return DTOs](../core/data/src/commonMain/kotlin/app/devper/pharm/data/remote/dto/SaleHistoryDto.kt) and pharmacy API currently have no return request ID. USER can submit returns. | Keep USER return permission with bill reference, actor, and reason; the same return intent must be idempotent across retries. Void remains ADMIN+. |
| Offline delivery | KMP [OfflineSaleQueueImpl](../core/data/src/commonMain/kotlin/app/devper/pharm/data/storage/OfflineSaleQueueImpl.kt) stores all entries in one JSON value and removes it on decode failure. | Preserve each unconfirmed entry for recovery, distinguish retryable delivery errors from business conflicts, and do not present a locally queued sale as confirmed. |
| End-of-day close | KMP [ReportsApi](../core/data/src/commonMain/kotlin/app/devper/pharm/data/remote/api/ReportsApi.kt) calls `POST /report/eod/close`; pharmacy API currently registers only `GET /report/eod`. | Sales owns a durable close record; Reporting reads it, and late sales produce auditable adjustments. |
| Contract checks | UM has OpenAPI; pharmacy API has routes and README descriptions but no OpenAPI file. The three repos run separate CI, and KMP's mock API is a UI fixture rather than a live contract check. | Each API owns its OpenAPI and checks route/response drift in every affected PR; KMP checks its used endpoints/DTOs. Run a three-service checkout and authorization smoke test nightly and before release. |

The [context map](../CONTEXT-MAP.md) defines business ownership; the [architecture notes](./ARCHITECTURE-NOTES.md) list implementation gaps and acceptance criteria. Pharmacy endpoint details belong in `pharmacy-api`, and identity endpoint details belong in `um-api`.

## Pharmacy permission split

The current pharmacy router has one broad ADMIN group. The agreed target splits it by operation; this table is a design contract, not the current router behavior.

| Target permission | Representative pharmacy routes |
| --- | --- |
| USER and above | Create sales; return items against a bill with actor and reason; read ordinary drug catalog. |
| MANAGER and above | Stock counts, adjustments and lots; goods receipt and suppliers; customer profile edits; label printing; operational `/report/slow-drugs` read. |
| ADMIN and above | Drug identity and selling-price edits; whole-bill void; KY administration; store-setting writes; `/report/summary`, `/report/dashboard`, `/report/daily`, `/report/monthly`, and `/report/top-drugs`. |
| Current session required within 60 seconds | Every write and sensitive read, including customer data, sale history, bill items, customer-bearing receipts, KY, financial and user data, and business-rule settings. Ordinary catalog reads may use a valid signed token during an identity-service outage. |
