package app.devper.pharm.presentation.users.exception

import app.devper.pharm.common.AppException

sealed class UserFormUiStateError(message: String, cause: Throwable? = null) : AppException(message, cause) {
    class NotFound : UserFormUiStateError("users.form_not_found")
}
