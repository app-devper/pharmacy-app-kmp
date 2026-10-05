# Validate cashier submission time

Capture the cashier submission time and preserve it through offline replay, alongside the backend's sale confirmation time. For online sales, start with a ±5-minute clock-skew tolerance. For offline sales, use trustworthy time evidence where available; if the submission time cannot be supported, send the sale for authorized review. The backend must not silently assign a suspicious sale to another business day or rewrite a closed period.
