package app.devper.pharm.presentation.saleshistory.i18n

import app.devper.pharm.presentation.saleshistory.exception.SalesHistoryUiStateError
import app.devper.pharm.ui.i18n.PharmStrings

fun SalesHistoryUiStateError.localize(s: PharmStrings): String = when (this) {
    is SalesHistoryUiStateError.LoadBillsFailed -> s.salesHistoryLoadBillsFailed
    is SalesHistoryUiStateError.LoadItemsFailed -> s.salesHistoryLoadItemsFailed
    is SalesHistoryUiStateError.SubmitReturnFailed -> s.salesHistorySubmitReturnFailed
}
