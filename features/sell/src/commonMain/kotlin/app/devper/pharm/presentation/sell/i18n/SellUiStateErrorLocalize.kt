package app.devper.pharm.presentation.sell.i18n

import app.devper.pharm.common.AppException
import app.devper.pharm.domain.validation.SaleValidationError
import app.devper.pharm.presentation.sell.exception.CheckoutUiStateError
import app.devper.pharm.presentation.sell.exception.CustomerPickerUiStateError
import app.devper.pharm.presentation.sell.exception.DrugPickerUiStateError
import app.devper.pharm.presentation.sell.exception.VoidSaleUiStateError
import app.devper.pharm.ui.i18n.PharmStrings
import app.devper.pharm.ui.i18n.localizeCommon

fun AppException.localizeSell(s: PharmStrings): String = when (this) {
    is SaleValidationError.EmptyCart -> s.sellCheckoutEmptyCart
    is SaleValidationError.VoidReasonRequired -> s.sellVoidReasonRequired
    is VoidSaleUiStateError.MissingBillId -> s.sellVoidMissingBillId
    is VoidSaleUiStateError.ReasonRequired -> s.sellVoidReasonRequired
    is VoidSaleUiStateError.VoidFailed -> s.sellVoidFailed
    is DrugPickerUiStateError.BarcodeNotFound -> s.sellBarcodeNotFound(code)
    is CustomerPickerUiStateError.LoadCustomersFailed -> s.sellLoadCustomersFailed
    is CheckoutUiStateError.PrintReceiptUnsupported -> s.sellPrintReceiptUnsupported
    is CheckoutUiStateError.KyIncomplete -> s.sellKyIncomplete(billNo, failed.joinToString("\n"))
    is CheckoutUiStateError.KyError -> s.sellKyError(billNo, cause?.message ?: s.commonErrorGeneric)
    is CheckoutUiStateError.OfflineSaved -> s.sellOfflineSaved
    is CheckoutUiStateError.CheckoutFailed -> s.sellCheckoutFailed
    else -> localizeCommon(s)
}
