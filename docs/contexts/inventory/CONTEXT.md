# Inventory

Inventory is the context for the pharmacy's physical stock, including quantities, lots, counts, and changes to them.

## Language

**Stock movement**:
A view of a change to physical stock with its cause, such as a sale, return, adjustment, import, or write-off. It does not imply a separate stock ledger.
_Avoid_: Sale, stock ledger

**Current stock**:
The authoritative current quantity of a drug, which may be negative after an explicitly confirmed oversell.
_Avoid_: Stock movement total

**Oversold quantity**:
The part of a confirmed sale that exceeded available stock and must be reconciled when stock arrives.
_Avoid_: Inventory error
