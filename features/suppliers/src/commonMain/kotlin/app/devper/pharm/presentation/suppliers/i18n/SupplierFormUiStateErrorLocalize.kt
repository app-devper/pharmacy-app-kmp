package app.devper.pharm.presentation.suppliers.i18n

import app.devper.pharm.common.AppException
import app.devper.pharm.presentation.suppliers.exception.SupplierFormUiStateError
import app.devper.pharm.ui.i18n.PharmStrings
import app.devper.pharm.ui.i18n.localizeCommon

fun AppException.localizeSupplierForm(s: PharmStrings): String = when (this) {
    is SupplierFormUiStateError.NotFound -> s.suppliersFormNotFound
    else -> localizeCommon(s)
}
