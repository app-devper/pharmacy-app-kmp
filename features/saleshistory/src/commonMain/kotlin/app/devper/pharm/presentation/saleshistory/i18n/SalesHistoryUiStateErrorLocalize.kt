package app.devper.pharm.presentation.saleshistory.i18n

import app.devper.pharm.common.AppException
import app.devper.pharm.domain.validation.SaleValidationError
import app.devper.pharm.presentation.saleshistory.exception.SalesHistoryUiStateError
import app.devper.pharm.ui.i18n.PharmStrings
import app.devper.pharm.ui.i18n.localizeCommon

fun AppException.localizeSalesHistory(s: PharmStrings): String = when (this) {
    is SaleValidationError.ReturnReasonRequired -> s.salesHistoryReturnReasonRequired
    is SaleValidationError.ReturnItemsRequired -> s.salesHistoryReturnItemsRequired
    is SalesHistoryUiStateError.LoadBillsFailed -> s.salesHistoryLoadBillsFailed
    is SalesHistoryUiStateError.LoadItemsFailed -> s.salesHistoryLoadItemsFailed
    is SalesHistoryUiStateError.SubmitReturnFailed -> s.salesHistorySubmitReturnFailed
    else -> localizeCommon(s)
}
