# Preserve unsynced sales on queue corruption

An offline sale is an unconfirmed commercial intent that may be the only local record of a cashier's work. Store entries independently and durably rather than as one all-or-nothing JSON list. If an entry cannot be decoded, preserve its raw data for export and recovery, alert the operator, and stop automatic replay of that damaged entry. Never silently discard an unconfirmed sale. The current queue implementation clears malformed data, so recovery behavior remains implementation work.
