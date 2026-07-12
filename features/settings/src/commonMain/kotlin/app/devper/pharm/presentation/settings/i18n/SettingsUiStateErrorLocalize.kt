package app.devper.pharm.presentation.settings.i18n

import app.devper.pharm.common.AppException
import app.devper.pharm.presentation.settings.exception.SettingsUiStateError
import app.devper.pharm.ui.i18n.PharmStrings
import app.devper.pharm.ui.i18n.localizeCommon

fun AppException.localizeSettings(s: PharmStrings): String = when (this) {
    is SettingsUiStateError.LoadSettingsFailed -> s.settingsLoadFailed
    is SettingsUiStateError.TestPrintFailed -> s.settingsTestPrintFailed
    else -> localizeCommon(s)
}
