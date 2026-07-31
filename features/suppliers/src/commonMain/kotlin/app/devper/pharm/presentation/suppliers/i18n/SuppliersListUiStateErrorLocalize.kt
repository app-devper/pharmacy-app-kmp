package app.devper.pharm.presentation.suppliers.i18n

import app.devper.pharm.common.AppException
import app.devper.pharm.presentation.suppliers.exception.SuppliersListUiStateError
import app.devper.pharm.ui.i18n.PharmStrings
import app.devper.pharm.ui.i18n.localizeCommon

fun AppException.localizeSuppliersList(s: PharmStrings): String = when (this) {
    is SuppliersListUiStateError.LoadSuppliersFailed -> s.suppliersListLoadFailed
    else -> localizeCommon(s)
}
