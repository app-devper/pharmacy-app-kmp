package app.devper.pharm.presentation.auth.exception

import app.devper.pharm.common.AppException

sealed class LoginUiStateError(message: String, cause: Throwable? = null) : AppException(message, cause) {
    class RequiredFields : LoginUiStateError("login.required_fields")
    class LoginFailed(cause: Throwable? = null) : LoginUiStateError("login.failed", cause)
}
