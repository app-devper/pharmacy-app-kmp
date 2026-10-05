# Backend confirmation of sales and stock

The backend is the authority for whether a sale has succeeded and its stock change has committed. The client can retain and retry a pending sale while offline, but must not present that local queue entry as a completed sale. Sales and Inventory remain separate domain contexts even though the backend currently records a sale and applies its stock changes in one transaction. This keeps the commercial outcome and physical stock concepts distinct while preserving an atomic result for the cashier.
