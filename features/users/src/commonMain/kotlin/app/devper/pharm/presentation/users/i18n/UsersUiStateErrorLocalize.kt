package app.devper.pharm.presentation.users.i18n

import app.devper.pharm.common.AppException
import app.devper.pharm.presentation.users.exception.UsersUiStateError
import app.devper.pharm.ui.i18n.PharmStrings
import app.devper.pharm.ui.i18n.localizeCommon

fun AppException.localizeUsers(s: PharmStrings): String = when (this) {
    is UsersUiStateError.LoadUsersFailed -> s.usersListLoadFailed
    is UsersUiStateError.RoleChangeFailed -> s.usersRoleChangeFailed
    is UsersUiStateError.StatusChangeFailed -> s.usersStatusChangeFailed
    is UsersUiStateError.SetPasswordFailed -> s.usersSetPasswordFailed
    else -> localizeCommon(s)
}
