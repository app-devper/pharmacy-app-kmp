package app.devper.pharm.presentation.users.exception

import app.devper.pharm.common.AppException

sealed class UsersUiStateError(message: String, cause: Throwable? = null) : AppException(message, cause) {
    class LoadUsersFailed(cause: Throwable? = null) : UsersUiStateError("users.load_failed", cause)
    class RoleChangeFailed(cause: Throwable? = null) : UsersUiStateError("users.role_change_failed", cause)
    class StatusChangeFailed(cause: Throwable? = null) : UsersUiStateError("users.status_change_failed", cause)
    class SetPasswordFailed(cause: Throwable? = null) : UsersUiStateError("users.set_password_failed", cause)
}
