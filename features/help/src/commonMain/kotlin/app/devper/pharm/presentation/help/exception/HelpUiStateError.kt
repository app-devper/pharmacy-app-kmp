package app.devper.pharm.presentation.help.exception

import app.devper.pharm.common.AppException

sealed class HelpUiStateError(message: String, cause: Throwable? = null) : AppException(message, cause) {
    class LoadFailed(cause: Throwable? = null) : HelpUiStateError("help.load_failed", cause)
}
