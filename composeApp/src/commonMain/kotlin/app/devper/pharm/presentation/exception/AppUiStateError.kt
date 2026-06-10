package app.devper.pharm.presentation.exception

import app.devper.pharm.common.AppException

sealed class AppUiStateError(message: String, cause: Throwable? = null) : AppException(message, cause) {
    class LogoutFailed(cause: Throwable? = null) : AppUiStateError("app.logout_failed", cause)
    class ThemeChangeFailed(cause: Throwable? = null) : AppUiStateError("app.theme_change_failed", cause)
}
