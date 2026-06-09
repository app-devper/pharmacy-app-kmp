package app.devper.pharm.presentation.saleshistory.exception

import app.devper.pharm.common.AppException

sealed class SalesHistoryUiStateError(message: String, cause: Throwable? = null) : AppException(message, cause) {
    class LoadBillsFailed(cause: Throwable? = null) : SalesHistoryUiStateError("saleshistory.load_bills_failed", cause)
    class LoadItemsFailed(cause: Throwable? = null) : SalesHistoryUiStateError("saleshistory.load_items_failed", cause)
    class SubmitReturnFailed(cause: Throwable? = null) : SalesHistoryUiStateError("saleshistory.submit_return_failed", cause)
}
