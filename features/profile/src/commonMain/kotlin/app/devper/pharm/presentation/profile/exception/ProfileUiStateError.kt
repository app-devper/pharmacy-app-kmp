package app.devper.pharm.presentation.profile.exception

import app.devper.pharm.common.AppException

sealed class ProfileUiStateError(message: String, cause: Throwable? = null) : AppException(message, cause) {
    class PasswordChangeFailed(cause: Throwable? = null) : ProfileUiStateError("profile.password_change_failed", cause)
}
