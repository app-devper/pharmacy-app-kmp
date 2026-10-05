# Sales

Sales is the context for recording a pharmacy sale and its commercial outcome.

## Language

**Sale**:
A confirmed exchange of goods for payment. Its confirmation is distinct from a cashier's intent to submit it.
_Avoid_: Pending sale, cart

**Pending sale**:
A cashier's submitted sale intent awaiting authoritative confirmation, including one retained for later delivery while offline.
_Avoid_: Completed sale

**Cashier submission time**:
The time the cashier submitted a sale intent, whether or not the sale has been confirmed.
_Avoid_: Sale confirmation time

**Sale confirmation time**:
The time the sale became confirmed by the authoritative system, which may be later than cashier submission when the client was offline.
_Avoid_: Cashier submission time

**Sale price snapshot**:
The price recorded for a drug in a confirmed sale, distinct from its configured price at a later time.
_Avoid_: Current catalog price

**Return**:
A reversal of some or all goods from a confirmed sale, with its own confirmed outcome.
_Avoid_: New sale, repeated return

**Commercial command**:
A sale or return submitted with a stable request identity, so a retry after a lost response yields the originally recorded outcome instead of a second transaction; the same identity with different content is refused.
_Avoid_: Retry, duplicate submission

**End-of-day close**:
A durable record that a responsible user closed a defined sales period at a particular time. It is distinct from viewing an end-of-day report.
_Avoid_: End-of-day report

**Late sale adjustment**:
An auditable change to an already closed sales period, recorded when a sale is confirmed on that day after it closed, a bill of that day is voided, or a return is made on that day after it closed. The close's own figures never change; the period's current figures are the close plus its adjustments.
_Avoid_: Silent close revision

**Sale sync conflict**:
A pending sale that cannot be confirmed under current business rules and requires an authorized person to resolve or cancel.
_Avoid_: Transient network failure

**Abandoned sale**:
A pending sale an authorized person decided will never be recorded, recorded on the server with its payload and reason; the same request is refused afterwards.
_Avoid_: Discarded sale, deleted sale

**KY pending**:
A recorded sale whose KY forms the server refused; it stays pending until the forms are recorded or closed with a reason.
_Avoid_: Failed sale

**Damaged pending sale**:
A pending sale whose stored data this device cannot read; it is kept raw for export and never replayed.
_Avoid_: Corrupt queue

**KY capture**:
What the cashier records for a sale's KY forms: the buyer, patient, prescription and pharmacist. It travels with the sale; the server records the forms with the sale and takes the drug, quantity, unit, paid value, date and ขย.10 balance from it (pharmacy-api ADR-0011).
_Avoid_: KY forms (the recorded register entries)
