# Context Map

This map records agreed domain boundaries across the pharmacy app and its connected API. It describes target ownership; [architecture notes](./docs/ARCHITECTURE-NOTES.md) identify where the current implementation differs.

## Contexts

- [Sales](./docs/contexts/sales/CONTEXT.md) — owns the commercial sale and its outcome.
- [Inventory](./docs/contexts/inventory/CONTEXT.md) — owns physical stock and its changes.
- [Purchasing](./docs/contexts/purchasing/CONTEXT.md) — owns purchase orders and their confirmation.
- [Compliance](./docs/contexts/compliance/CONTEXT.md) — owns required KY records.
- [Customers](./docs/contexts/customers/CONTEXT.md) — owns customer identity and profile.
- [Access](./docs/contexts/access/CONTEXT.md) — owns user roles and authorization policy.
- [Store Configuration](./docs/contexts/store-configuration/CONTEXT.md) — owns settings shared by a pharmacy tenant.
- [Catalog](./docs/contexts/catalog/CONTEXT.md) — owns drug identity, barcodes, and configured prices.

## Relationships

- **Sales → Inventory**: a confirmed sale requests a stock change; the two contexts retain distinct ownership even when the backend commits both in one transaction.
- **Client → backend**: the backend confirms whether a sale exists and its stock change has been committed. A locally queued sale is pending until that confirmation.
- **Purchasing → Inventory**: confirming a purchase order receives lots, increases stock, and reconciles earlier oversold quantities.
- **Purchasing → Compliance**: confirming a purchase order creates the required KY9 record.
- **Sales → Customers**: confirmed sales, returns, and voids provide the facts behind customer spend and visit summaries; Customers owns the profile, not the transactions.
- **Sales → Compliance (target contract)**: skipping KY capture during checkout creates an exception requiring review, not a completed compliance record. The current implementation records only a boolean.
- **Reporting → Sales and Inventory**: reports are read views of business facts and do not own the underlying transactions or stock.
- **KMP → Access API and pharmacy API**: both sides must enforce the same role policy; displaying a route in KMP does not grant backend authority.
- **KMP → backend deployment**: backend changes must preserve the contract used by deployed clients before a new KMP version relies on them.
- **UI features → domain contexts**: feature modules group screens and workflows; they do not redefine ownership of Sales, Inventory, Purchasing, or other domain concepts.
- **Sales → Reporting**: Sales owns the durable end-of-day close; Reports presents the close record and other read views.
- **Offline Sales → end-of-day close**: a confirmed sale belongs to the business day of cashier submission; confirmation after that day's close creates an auditable adjustment rather than silently changing the close record.
- **Inventory → print output**: labels may display current drug information, but the generated PDF does not own stock or price facts.
- **Catalog → Inventory**: Catalog identifies a drug and its configured prices; Inventory owns its on-hand quantity and lots.
- **Catalog → Sales**: Sales retains the cashier-approved transaction-time price; a later Catalog price change cannot silently reprice an offline sale.
- **Identity service → pharmacy API**: the identity service owns accounts, login, and role assignment; the pharmacy API enforces the agreed role policy on pharmacy operations.
- **Identity service → pharmacy API session status**: pharmacy writes and sensitive reads must reflect a revoked session, deactivated account, or changed role within 60 seconds.
- **Tenant → pharmacy data**: a SUPER role alone does not grant access across pharmacy tenants; any cross-tenant support requires explicit, auditable delegation to a named tenant.
- **Catalog, Sales, Compliance → output**: labels, receipts, and KY PDFs render facts from their owning contexts and do not become the source of those facts.

Supporting UI, reporting, printing, and export capabilities are mapped in [architecture notes](./docs/ARCHITECTURE-NOTES.md); they do not own the underlying business facts.
The current and target contracts between KMP, `pharmacy-api`, and `um-api` are mapped in [cross-repository contracts](./docs/INTEGRATION-CONTRACTS.md).
