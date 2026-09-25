# Current stock and movement view

Keep the backend's current stock quantity as the authoritative balance for now. A stock movement is a view assembled from the underlying business records, not a separate ledger whose sum defines stock. This preserves the present transactional model while still supporting investigation; a ledger migration needs a concrete audit requirement before taking on its consistency and migration costs. Explicitly confirmed oversells may make current stock negative, and later receipts must reconcile the oversold quantity.
