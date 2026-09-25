# Screen modules and domain ownership

Keep the existing feature modules organized around UI workflows while documenting business ownership separately in the context map. Screens often cross Sales, Inventory, Purchasing, and Compliance boundaries, so matching every UI module to one bounded context would create churn without clarifying the owner of data. Move code only when a concrete dependency violates an agreed boundary.
