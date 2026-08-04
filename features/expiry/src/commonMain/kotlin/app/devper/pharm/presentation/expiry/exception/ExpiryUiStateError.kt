package app.devper.pharm.presentation.expiry.exception

import app.devper.pharm.common.AppException

sealed class ExpiryUiStateError(message: String, cause: Throwable? = null) : AppException(message, cause) {
    class LoadLotsFailed(cause: Throwable? = null) : ExpiryUiStateError("expiry.load_lots_failed", cause)
    class WriteoffFailed(cause: Throwable? = null) : ExpiryUiStateError("expiry.writeoff_failed", cause)
}
