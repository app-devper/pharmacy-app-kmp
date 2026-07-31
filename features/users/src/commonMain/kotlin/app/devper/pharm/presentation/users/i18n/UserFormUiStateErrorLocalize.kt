package app.devper.pharm.presentation.users.i18n

import app.devper.pharm.common.AppException
import app.devper.pharm.presentation.users.exception.UserFormUiStateError
import app.devper.pharm.ui.i18n.PharmStrings
import app.devper.pharm.ui.i18n.localizeCommon

fun AppException.localizeUserForm(s: PharmStrings): String = when (this) {
    is UserFormUiStateError.NotFound -> s.usersFormNotFound
    is UserFormUiStateError.LoadUserFailed -> s.usersFormLoadFailed
    else -> localizeCommon(s)
}
