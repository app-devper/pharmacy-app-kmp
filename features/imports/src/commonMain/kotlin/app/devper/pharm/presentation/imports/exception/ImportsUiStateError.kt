package app.devper.pharm.presentation.imports.exception

import app.devper.pharm.common.AppException

sealed class ImportsUiStateError(message: String, cause: Throwable? = null) : AppException(message, cause) {
    class ConfirmFailed(cause: Throwable? = null) : ImportsUiStateError("imports.confirm_failed", cause)
}
