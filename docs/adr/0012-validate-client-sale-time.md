# Validate client sale time

Capture the cashier's sale time when the sale is submitted and preserve it through offline replay, alongside the backend confirmation time. For online sales, start with a ±5-minute clock-skew tolerance. For offline sales, assess the timestamp against the device's last recorded server-time anchor and clock history; if the interval cannot be supported, send the sale for authorized review. The backend must not silently assign a suspicious sale to another business day or rewrite a closed period.
