# Idempotent sale and return requests

Sales and returns can succeed on the backend while their responses are lost. Each submitted intent therefore needs a stable request identity: retrying the same sale or return must yield its original outcome rather than create a second transaction. Sales already use a client request ID; return requests do not yet meet this decision and require implementation across client and backend.
