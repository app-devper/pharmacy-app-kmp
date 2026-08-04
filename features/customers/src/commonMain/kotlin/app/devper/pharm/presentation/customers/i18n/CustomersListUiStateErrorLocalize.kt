package app.devper.pharm.presentation.customers.i18n

import app.devper.pharm.common.AppException
import app.devper.pharm.presentation.customers.exception.CustomersListUiStateError
import app.devper.pharm.ui.i18n.PharmStrings
import app.devper.pharm.ui.i18n.localizeCommon

fun AppException.localizeCustomersList(s: PharmStrings): String = when (this) {
    is CustomersListUiStateError.LoadCustomersFailed -> s.customersListLoadFailed
    else -> localizeCommon(s)
}
