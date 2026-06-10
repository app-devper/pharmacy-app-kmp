package app.devper.pharm.presentation.sell

import androidx.compose.runtime.Composable
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.input.key.Key
import app.devper.pharm.presentation.sell.flow.CheckoutUiState
import app.devper.pharm.presentation.sell.flow.CheckoutViewModel
import app.devper.pharm.presentation.sell.flow.CustomerPickerUiState
import app.devper.pharm.presentation.sell.flow.CustomerPickerViewModel
import app.devper.pharm.presentation.sell.flow.DrugPickerUiState
import app.devper.pharm.presentation.sell.flow.DrugPickerViewModel
import app.devper.pharm.presentation.sell.flow.ParkedCartUiState
import app.devper.pharm.presentation.sell.flow.ParkedCartViewModel
import app.devper.pharm.presentation.sell.flow.VoidSaleUiState
import app.devper.pharm.presentation.sell.flow.VoidSaleViewModel
import app.devper.pharm.ui.common.PharmShortcut

@Composable
fun rememberSellShortcuts(
    sellVM: SellViewModel,
    checkoutVM: CheckoutViewModel,
    drugPickerVM: DrugPickerViewModel,
    customerPickerVM: CustomerPickerViewModel,
    parkedCartVM: ParkedCartViewModel,
    voidSaleVM: VoidSaleViewModel,
    sellState: SellUiState,
    checkoutState: CheckoutUiState,
    drugState: DrugPickerUiState,
    customerState: CustomerPickerUiState,
    parkedState: ParkedCartUiState,
    voidState: VoidSaleUiState,
    shortcutsVisible: Boolean,
    onShowShortcuts: () -> Unit,
    onHideShortcuts: () -> Unit,
    hasError: Boolean,
    onDismissAllErrors: () -> Unit,
    searchFocus: FocusRequester,
): Array<PharmShortcut> {
    val onShortcutNewBill: () -> Unit = { parkedCartVM.newBillOnNextTab() }

    val onShortcutEscape: () -> Unit = {
        when (
            resolveSellEscapeAction(
                shortcutsVisible = shortcutsVisible,
                hasError = hasError,
                overwriteSlotPending = parkedState.overwriteSlot != null,
                clearConfirmVisible = sellState.showClearConfirm,
                skipKyConfirmVisible = checkoutState.showSkipKyConfirm,
                oversellPending = checkoutState.oversellPending != null,
                kyCapturePending = checkoutState.kyCapturePending != null,
                lineDiscountOpen = sellState.lineDiscountFor != null,
                cartDiscountOpen = sellState.cartDiscountSheetOpen,
                altUnitPickerOpen = drugState.altUnitPickerFor != null,
                voidSheetOpen = voidState.sheetOpen,
                customerOpen = customerState.open,
                parkedSheetOpen = parkedState.sheetOpen,
                receiptVisible = sellState.receipt != null,
            )
        ) {
            SellEscapeAction.HideShortcuts -> onHideShortcuts()
            SellEscapeAction.DismissErrors -> onDismissAllErrors()
            SellEscapeAction.CancelOverwrite -> parkedCartVM.cancelOverwrite()
            SellEscapeAction.CancelClearCart -> sellVM.cancelClearCart()
            SellEscapeAction.CancelSkipKy -> checkoutVM.cancelSkipKy()
            SellEscapeAction.DismissOversell -> checkoutVM.dismissOversell()
            SellEscapeAction.DismissKyCapture -> checkoutVM.dismissKyCapture()
            SellEscapeAction.CloseLineDiscount -> sellVM.onCloseLineDiscount()
            SellEscapeAction.CloseCartDiscount -> sellVM.onCloseCartDiscount()
            SellEscapeAction.CloseAltUnitPicker -> drugPickerVM.onCloseAltUnitPicker()
            SellEscapeAction.CloseVoidSheet -> voidSaleVM.closeSheet()
            SellEscapeAction.CloseCustomer -> customerPickerVM.close()
            SellEscapeAction.CloseParkedSheet -> parkedCartVM.closeSheet()
            SellEscapeAction.DismissReceipt -> checkoutVM.dismissReceipt()
            null -> {}
        }
    }

    return arrayOf(
        PharmShortcut(key = Key.F1, label = "F1", action = onShowShortcuts),
        PharmShortcut(key = Key.F2, label = "F2", action = { runCatching { searchFocus.requestFocus() } }),
        PharmShortcut(key = Key.F3, label = "F3", action = customerPickerVM::open),
        PharmShortcut(key = Key.F4, label = "F4", action = sellVM::onOpenCartDiscount),
        PharmShortcut(key = Key.F6, label = "F6", action = onShortcutNewBill),
        PharmShortcut(key = Key.F8, label = "F8", action = parkedCartVM::openSheet),
        PharmShortcut(key = Key.F9, label = "F9", action = { if (checkoutState.canCheckout) checkoutVM.submit() }),
        PharmShortcut(key = Key.Escape, label = "Esc", action = onShortcutEscape),
        PharmShortcut(key = Key.N, ctrl = true, label = "Ctrl+N", action = onShortcutNewBill),
        PharmShortcut(key = Key.P, ctrl = true, shift = true, label = "Ctrl+Shift+P", action = parkedCartVM::openSheet),
    )
}
