# Offline sale time and closed periods

Record both when a cashier made an offline sale and when the backend confirmed it. Attribute the sale to its original business day. If confirmation arrives after that day's end-of-day close, add an auditable adjustment to the closed period instead of silently rewriting the original close record. The current checkout request carries no sale time and the backend assigns its receipt time, so this contract requires coordinated client and backend changes.
