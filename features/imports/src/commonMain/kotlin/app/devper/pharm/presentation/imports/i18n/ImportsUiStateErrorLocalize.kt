package app.devper.pharm.presentation.imports.i18n

import app.devper.pharm.common.AppException
import app.devper.pharm.presentation.imports.exception.ImportFormUiStateError
import app.devper.pharm.presentation.imports.exception.ImportsUiStateError
import app.devper.pharm.ui.i18n.PharmStrings
import app.devper.pharm.ui.i18n.localizeCommon

fun AppException.localizeImports(s: PharmStrings): String = when (this) {
    is ImportsUiStateError.LoadOrdersFailed -> s.importsListLoadFailed
    is ImportsUiStateError.LoadOrderFailed -> s.importsDetailLoadFailed
    is ImportsUiStateError.ConfirmFailed -> s.importsConfirmFailed
    is ImportFormUiStateError.LoadOrderFailed -> s.importsFormLoadOrderFailed
    is ImportFormUiStateError.LoadDrugsFailed -> s.importsFormLoadDrugsFailed
    is ImportFormUiStateError.LoadSuppliersFailed -> s.importsFormLoadSuppliersFailed
    else -> localizeCommon(s)
}
