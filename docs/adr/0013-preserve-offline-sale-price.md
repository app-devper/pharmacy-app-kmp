# Preserve the offline sale price

Record the price the cashier and customer saw when the sale was made, and preserve it as the sale price snapshot through offline replay. A later Catalog price change must not silently change the amount of that sale. If the backend cannot validate the captured price under its sale policy, hold the pending sale as a conflict for authorized review rather than accepting a different total. The current backend recalculates price from the current drug record, so this requires an API and validation change.
