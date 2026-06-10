package app.devper.pharm.domain.validation

import app.devper.pharm.common.AppException

sealed class SaleValidationError(message: String) : AppException(message) {
    class EmptyCart : SaleValidationError("sale.empty_cart")
    class ReturnReasonRequired : SaleValidationError("sale.return_reason_required")
    class ReturnItemsRequired : SaleValidationError("sale.return_items_required")
    class VoidReasonRequired : SaleValidationError("sale.void_reason_required")
}
