# Resolve offline sale conflicts explicitly

Separate retryable delivery failures from business conflicts during offline sale replay. Preserve the original payload and stop automatic retry for a conflicted entry. An authorized person must resolve or cancel it with a recorded reason; neither silent discard nor endless retry is an acceptable outcome for an unconfirmed sale. Current replay records every failure as a generic error, so typed conflict handling and resolution remain implementation work.
