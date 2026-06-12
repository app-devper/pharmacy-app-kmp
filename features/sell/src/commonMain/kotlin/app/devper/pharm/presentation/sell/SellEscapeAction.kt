package app.devper.pharm.presentation.sell

enum class SellEscapeAction {
    HideShortcuts,
    DismissErrors,
    CancelOverwrite,
    CancelClearCart,
    CancelSkipKy,
    DismissOversell,
    DismissKyCapture,
    ClosePayment,
    CloseLineDiscount,
    CloseCartDiscount,
    CloseAltUnitPicker,
    CloseVoidSheet,
    CloseCustomer,
    CloseParkedSheet,
    DismissReceipt,
}

fun resolveSellEscapeAction(
    shortcutsVisible: Boolean,
    hasError: Boolean,
    overwriteSlotPending: Boolean,
    clearConfirmVisible: Boolean,
    skipKyConfirmVisible: Boolean,
    oversellPending: Boolean,
    kyCapturePending: Boolean,
    paymentOpen: Boolean,
    lineDiscountOpen: Boolean,
    cartDiscountOpen: Boolean,
    altUnitPickerOpen: Boolean,
    voidSheetOpen: Boolean,
    customerOpen: Boolean,
    parkedSheetOpen: Boolean,
    receiptVisible: Boolean,
): SellEscapeAction? = when {
    shortcutsVisible -> SellEscapeAction.HideShortcuts
    hasError -> SellEscapeAction.DismissErrors
    overwriteSlotPending -> SellEscapeAction.CancelOverwrite
    clearConfirmVisible -> SellEscapeAction.CancelClearCart
    skipKyConfirmVisible -> SellEscapeAction.CancelSkipKy
    oversellPending -> SellEscapeAction.DismissOversell
    kyCapturePending -> SellEscapeAction.DismissKyCapture
    paymentOpen -> SellEscapeAction.ClosePayment
    lineDiscountOpen -> SellEscapeAction.CloseLineDiscount
    cartDiscountOpen -> SellEscapeAction.CloseCartDiscount
    altUnitPickerOpen -> SellEscapeAction.CloseAltUnitPicker
    voidSheetOpen -> SellEscapeAction.CloseVoidSheet
    customerOpen -> SellEscapeAction.CloseCustomer
    parkedSheetOpen -> SellEscapeAction.CloseParkedSheet
    receiptVisible -> SellEscapeAction.DismissReceipt
    else -> null
}
