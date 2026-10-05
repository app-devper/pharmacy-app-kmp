# Offline submission time and closed periods

Record both the cashier submission time of an offline sale intent and the backend's sale confirmation time. Once confirmed, attribute the sale to the business day of the cashier submission. If confirmation arrives after that day's end-of-day close, add an auditable adjustment to the closed period instead of silently rewriting the original close record. The current checkout request carries no cashier submission time and the backend assigns its receipt time, so this contract requires coordinated client and backend changes.
