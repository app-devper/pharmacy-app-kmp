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

## Implementation gaps identified so far

- Return requests lack the agreed retry identity and can create a second return after an ambiguous response.
- The pharmacy API rejects MANAGER on administrative routes while KMP exposes administrative navigation to that role.
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

## Checks required before implementation

- Verify the applicable KY requirements before enforcing who may skip capture or resolve an exception; the role decision here does not establish legal compliance.
- Turn the agreed MANAGER policy into an explicit route-by-route permission matrix shared by the identity service, pharmacy API, and KMP navigation.
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
