package app.devper.pharm.presentation.customers.i18n

import app.devper.pharm.common.AppException
import app.devper.pharm.presentation.customers.exception.CustomerDetailUiStateError
import app.devper.pharm.ui.i18n.PharmStrings
import app.devper.pharm.ui.i18n.localizeCommon

fun AppException.localizeCustomerDetail(s: PharmStrings): String = when (this) {
    is CustomerDetailUiStateError.CustomerNotFound -> s.customersDetailNotFound
    is CustomerDetailUiStateError.LoadCustomerFailed -> s.customersDetailLoadCustomerFailed
    is CustomerDetailUiStateError.LoadSalesFailed -> s.customersDetailLoadSalesFailed
    else -> localizeCommon(s)
}
