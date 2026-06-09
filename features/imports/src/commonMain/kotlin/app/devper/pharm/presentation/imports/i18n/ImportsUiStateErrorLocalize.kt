package app.devper.pharm.presentation.imports.i18n

import app.devper.pharm.common.AppException
import app.devper.pharm.presentation.imports.exception.ImportsUiStateError
import app.devper.pharm.ui.i18n.PharmStrings
import app.devper.pharm.ui.i18n.localizeCommon

fun AppException.localizeImports(s: PharmStrings): String = when (this) {
    is ImportsUiStateError.ConfirmFailed -> s.importsConfirmFailed
    else -> localizeCommon(s)
}
