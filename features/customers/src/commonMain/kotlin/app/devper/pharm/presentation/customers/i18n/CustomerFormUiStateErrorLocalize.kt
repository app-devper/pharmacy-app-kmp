package app.devper.pharm.presentation.customers.i18n

import app.devper.pharm.common.AppException
import app.devper.pharm.presentation.customers.exception.CustomerFormUiStateError
import app.devper.pharm.ui.i18n.PharmStrings
import app.devper.pharm.ui.i18n.localizeCommon

fun AppException.localizeCustomerForm(s: PharmStrings): String = when (this) {
    is CustomerFormUiStateError.LoadCustomerFailed -> s.customersFormLoadFailed
    is CustomerFormUiStateError.NotFound -> s.customersFormNotFound
    else -> localizeCommon(s)
}
