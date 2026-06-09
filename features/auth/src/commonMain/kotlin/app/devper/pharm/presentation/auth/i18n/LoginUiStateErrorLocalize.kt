package app.devper.pharm.presentation.auth.i18n

import app.devper.pharm.common.AppException
import app.devper.pharm.presentation.auth.exception.LoginUiStateError
import app.devper.pharm.ui.i18n.PharmStrings
import app.devper.pharm.ui.i18n.localizeCommon

fun AppException.localizeLogin(s: PharmStrings): String = when (this) {
    is LoginUiStateError.RequiredFields -> s.loginRequiredFields
    is LoginUiStateError.LoginFailed -> s.loginFailed
    else -> localizeCommon(s)
}
