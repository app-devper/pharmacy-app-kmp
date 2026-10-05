# Catalog and Inventory ownership

Catalog owns drug identity, barcodes, and configured selling prices; Inventory owns on-hand stock and lots. Sales retains the price used for each confirmed transaction. The current Drug model combines these fields for convenience, but that shape does not transfer ownership of stock to Catalog or ownership of prices to Inventory. This distinction keeps price edits and stock-changing operations from being treated as the same business action.
