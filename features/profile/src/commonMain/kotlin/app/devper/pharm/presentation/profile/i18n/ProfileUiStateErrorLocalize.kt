package app.devper.pharm.presentation.profile.i18n

import app.devper.pharm.common.AppException
import app.devper.pharm.presentation.profile.exception.ProfileUiStateError
import app.devper.pharm.ui.i18n.PharmStrings
import app.devper.pharm.ui.i18n.localizeCommon

fun AppException.localizeProfile(s: PharmStrings): String = when (this) {
    is ProfileUiStateError.PasswordChangeFailed -> s.profilePasswordChangeFailed
    else -> localizeCommon(s)
}
