# Architecture notes

These are agreed working rules from the whole-project design interview. The context map defines business ownership; ADRs explain decisions that are costly to reverse. Items listed as gaps are decisions accepted here but not yet implemented.

## Working rules

- Prioritize correctness of sales and stock, then maintainability. Add complexity for performance when a measured problem justifies it.
- Reports read Sales and Inventory facts; they do not own transactions or stock. Show the data period or freshness time, and make a failed refresh visible.
- Store settings are tenant-wide business configuration. UI preferences such as language and theme are presentation choices.
- Keep backend contracts for every declared-supported client version. Release compatible backend changes before clients that depend on them.
- Treat iOS as a supported platform only with an iOS compile gate on pull requests to `develop` and `main`.
- Supplier identity belongs to Purchasing. Purchase orders should reference a supplier ID and retain the ordered-time name for historical reading.
- Barcode labels are print output, not authoritative stock records; refresh drug and price details before printing.
- Labels, receipts, and KY PDFs are supporting outputs of Catalog, Sales, and Compliance respectively.

## User-visible quality bar

- Prove three end-to-end paths against the real backend or an equivalent environment before calling the release usable: checkout through stock change and receipt; offline checkout through sync or conflict resolution; goods receipt through lot and stock increase.
- Every platform must display the receipt after a confirmed checkout. Verify physical printing on Web and JVM; Android and iOS must clearly state that printing is unavailable until their printer integrations exist.
- Use a repeatable fixture of about 10,000 catalog items, a recorded mid-range Android device model, and a fixed test network. Measure from a typed search character to visible results; the initial p95 target is 300 ms.
- A checkout tap must produce immediate visible feedback. Measure from the tap to backend confirmation; on the fixed normal network the initial p95 target is 3 seconds.
- Record a performance baseline before adopting caching or derived-state optimizations.
- Test comprehension with at least five new cashiers performing drug search, checkout, returns, and offline sale resolution without hints. Checkout and offline resolution must succeed without guessing the next action. Distinguish retryable failures from conflicts needing authorized review, and show when retained report data was last refreshed successfully. Test goods receipt and end-of-day close with store administrators as a second group.

## Implementation gaps identified so far

- Return requests lack the agreed retry identity and can create a second return after an ambiguous response.
- The pharmacy API rejects MANAGER on administrative routes while KMP exposes administrative navigation to that role.
- The pharmacy API currently exposes six report routes to USER; the agreed policy moves `summary`, `dashboard`, `daily`, `monthly`, and `top-drugs` to ADMIN+ and `slow-drugs` to MANAGER+.
- KY skip currently records a boolean, without reason, actor, time, review state, or resolution.
- A malformed offline sale queue is currently cleared instead of preserved for recovery.
- Reports do not expose a clear freshness time or failed-refresh state.
- CI does not currently require an iOS compile check.
- Client-version telemetry and the minimum-version enforcement needed to apply the agreed support floor are not yet available.
- KMP exposes an end-of-day close command, but the current pharmacy API exposes only the end-of-day report read route.
- Purchase orders currently store a supplier name rather than linking to a supplier ID.
- Offline queue storage is one JSON list without a raw export or per-entry recovery path.
- Offline checkout requests do not carry cashier submission time; the backend currently assigns the replay time as sale confirmation time, so late sync changes the EOD day.
- Offline replay records business conflicts as generic failures, without a separate review-and-resolution path.
- Drug identity/prices and stock are combined in the current model even though Catalog and Inventory have separate ownership.
- Offline requests lack cashier submission time and backend clock-skew review. The agreed online starting tolerance is ±5 minutes; offline sales without trustworthy time evidence need authorized review.
- The backend currently re-resolves the configured price when an offline sale arrives, rather than preserving the cashier-approved price snapshot.
- Android and iOS receipt-printer implementations currently return unsupported; the agreed receipt-display rule still needs verification on each platform.
- The available mock API fixture has only six drugs; no repeatable 10,000-item search fixture or end-to-end performance harness is present.
- Offline sync displays raw errors with the same Retry/Cancel actions for transient failures and business conflicts, and a failed refresh can leave the page without a retry action.
- Reports can retain old values after a failed refresh without showing when those values were last confirmed.
- `um-api` reloads the current user and session on its protected requests, while `pharmacy-api` authorizes pharmacy requests from JWT claims without checking whether the UM session or role was revoked. A role change or deactivation can therefore remain effective in pharmacy operations until the token expires.
- Pharmacy returns are currently allowed for USER and above; the agreed policy retains this but requires a bill reference, actor, reason, and retry identity. Whole-bill void remains ADMIN+.
- KMP, `pharmacy-api`, and `um-api` have separate CI checks, with no cross-repository API contract gate.
- The target 60-second revocation bound for pharmacy writes and sensitive reads is not enforced by `pharmacy-api` today.
- `um-api` has no dedicated session-verification endpoint for other services; `pharmacy-api` has no 30-second session-status cache.
- `um-api` has an OpenAPI contract, while `pharmacy-api` has no OpenAPI file; KMP's mock API and unit tests do not check the live three-service contract.

## Checks required before implementation

- Verify the applicable KY requirements before enforcing who may skip capture or resolve an exception; the role decision here does not establish legal compliance.
- Turn the agreed MANAGER policy into an explicit route-by-route permission matrix shared by the identity service, pharmacy API, and KMP navigation.
- Add a dedicated UM session/role verification endpoint and a pharmacy API cache keyed by session ID for at most 30 seconds. Do not share UM Redis or databases with the pharmacy API.
- Keep the product-wide context map and ADRs here, maintain endpoint contracts in their owning API repos, link the three READMEs, and add cross-repository checks for the API contracts KMP consumes.
- Treat customer identifying data, sale history, bill items and customer-bearing receipts, KY, financial reports, user data, business-rule settings, and every write as requiring current permission. On identity-service outage after the cached check expires, stop confirming these operations; any client-retained sale remains pending. An ordinary catalog read may continue under a valid signed token.
- Add pharmacy API OpenAPI with route/response checks, keep UM API OpenAPI in its owner repo, and verify KMP's used endpoints and DTOs in each affected PR. Run a three-service checkout/authorization smoke test nightly and before release rather than on every PR.
- Define the recovery and review procedures for corrupted or conflicted offline entries without discarding the original sale intent.
- Confirm the available device clock and server-time evidence on each platform before relying on offline submission timestamps. A persisted server-time anchor and clock history are candidate mechanisms, not yet verified implementation requirements.
- Measure deployed client versions before retiring an API contract under the initial support floor.

## Recommended implementation order

1. Protect money and sale intent: make returns idempotent, preserve damaged offline entries, and distinguish replay conflicts from retryable failures.
2. Make the sale contract explicit across KMP and backend: preserve cashier submission time and approved price, validate clock evidence, and reconcile late sales against closed periods. Align the end-of-day close endpoint with the durable close model.
3. Align authorization and compliance: define route-level MANAGER permissions, then build reviewable KY exceptions after checking the applicable requirements.
4. Tighten other ownership and visibility: connect purchase orders to supplier IDs, show report freshness, and keep Catalog price changes separate from Inventory stock changes.
5. Strengthen release confidence: require iOS compilation and measure active client versions before retiring old API behavior.

## Feature-module map

- **Sales**: `sell`, `saleshistory`, `offlinesync`.
- **Inventory**: `stock`, `stockcount`, `expiry`, `movements`, `planning`; `stock` also edits Catalog facts, and `planning` crosses into Purchasing.
- **Catalog and output**: `labels` reads Catalog facts and creates print output.
- **Purchasing**: `imports`, `bulkimport`, `suppliers`.
- **Compliance**: `ky`.
- **Customers**: `customers`.
- **Access**: `auth`, `users`, account portions of `profile`.
- **Store Configuration**: business-setting portions of `settings`.
- **Read and presentation areas**: `reports` reads across contexts and currently hosts the Sales end-of-day close UI; UI preferences in `profile` are presentation choices; `help` is product documentation.
