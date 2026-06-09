package app.devper.pharm.presentation.sell.exception

import app.devper.pharm.common.AppException

sealed class VoidSaleUiStateError(message: String, cause: Throwable? = null) : AppException(message, cause) {
    class MissingBillId : VoidSaleUiStateError("sell.void_missing_bill_id")
    class ReasonRequired : VoidSaleUiStateError("sell.void_reason_required")
    class VoidFailed(cause: Throwable? = null) : VoidSaleUiStateError("sell.void_failed", cause)
}

sealed class DrugPickerUiStateError(message: String, cause: Throwable? = null) : AppException(message, cause) {
    class BarcodeNotFound(val code: String) : DrugPickerUiStateError("sell.barcode_not_found")
}

sealed class CustomerPickerUiStateError(message: String, cause: Throwable? = null) : AppException(message, cause) {
    class LoadCustomersFailed(cause: Throwable? = null) : CustomerPickerUiStateError("sell.load_customers_failed", cause)
}

sealed class CheckoutUiStateError(message: String, cause: Throwable? = null) : AppException(message, cause) {
    class PrintReceiptUnsupported : CheckoutUiStateError("sell.print_receipt_unsupported")
    class KyIncomplete(val billNo: String, val failed: List<String>) : CheckoutUiStateError("sell.ky_incomplete")
    class KyError(val billNo: String, cause: Throwable? = null) : CheckoutUiStateError("sell.ky_error", cause)
    class OfflineSaved : CheckoutUiStateError("sell.offline_saved")
    class CheckoutFailed(cause: Throwable? = null) : CheckoutUiStateError("sell.checkout_failed", cause)
}
