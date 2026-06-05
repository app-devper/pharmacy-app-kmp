package app.devper.pharm.presentation.sell
import app.devper.pharm.presentation.sell.sibling.CheckoutViewModel
import app.devper.pharm.presentation.sell.sibling.VoidSaleViewModel
import app.devper.pharm.presentation.sell.sibling.ParkedCartViewModel
import app.devper.pharm.presentation.sell.sibling.CustomerPickerViewModel
import app.devper.pharm.presentation.sell.sibling.DrugPickerViewModel

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Surface
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.unit.dp
import app.devper.pharm.domain.model.CartLine
import app.devper.pharm.domain.model.CartLineKey
import app.devper.pharm.ui.common.LocalPharmSnackbar
import app.devper.pharm.ui.common.PharmShortcut
import app.devper.pharm.ui.common.PharmToast
import app.devper.pharm.ui.common.ToastAction
import app.devper.pharm.ui.common.pharmShortcuts
import app.devper.pharm.ui.components.ErrorBottomSheet
import app.devper.pharm.ui.scanner.scanBarcodes
import app.devper.pharm.presentation.sell.components.AltUnitPickerSheet
import app.devper.pharm.presentation.sell.components.CartDiscountSheet
import app.devper.pharm.presentation.sell.components.CartFooterBar
import app.devper.pharm.presentation.sell.components.CartPanel
import app.devper.pharm.presentation.sell.components.CartSlotRail
import app.devper.pharm.presentation.sell.components.CustomerPickerSheet
import app.devper.pharm.presentation.sell.components.DrugPickerColumn
import app.devper.pharm.presentation.sell.components.KyCaptureSheet
import app.devper.pharm.presentation.sell.components.SkipKyConfirmSheet
import app.devper.pharm.presentation.sell.components.LineDiscountSheet
import app.devper.pharm.presentation.sell.components.OversellConfirmSheet
import app.devper.pharm.presentation.sell.components.ParkOverwriteDialog
import app.devper.pharm.presentation.sell.components.SwapToParkedDialog
import app.devper.pharm.presentation.sell.components.ParkedCartsSheet
import app.devper.pharm.presentation.sell.components.ReceiptDialog
import app.devper.pharm.presentation.sell.components.ShortcutLegend
import app.devper.pharm.presentation.sell.components.VoidReasonSheet
import app.devper.pharm.ui.theme.pharmTokens
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SellScreen(
    onOpenCart: () -> Unit,
    sellVM: SellViewModel = koinViewModel(),
    checkoutVM: CheckoutViewModel = koinViewModel(),
    drugPickerVM: DrugPickerViewModel = koinViewModel(),
    customerPickerVM: CustomerPickerViewModel = koinViewModel(),
    parkedCartVM: ParkedCartViewModel = koinViewModel(),
    voidSaleVM: VoidSaleViewModel = koinViewModel(),
) {
    val sellState by sellVM.state.collectAsStateWithLifecycle()
    val checkoutState by checkoutVM.state.collectAsStateWithLifecycle()
    val drugState by drugPickerVM.state.collectAsStateWithLifecycle()
    val customerState by customerPickerVM.state.collectAsStateWithLifecycle()
    val parkedState by parkedCartVM.state.collectAsStateWithLifecycle()
    val voidState by voidSaleVM.state.collectAsStateWithLifecycle()

    val t = pharmTokens
    val searchFocus = remember { FocusRequester() }
    var showShortcuts by remember { mutableStateOf(false) }

    val snackbar = LocalPharmSnackbar.current
    val onTapParkSlot: (Int) -> Unit = { slot ->
        val willPark = parkedState.parkedSlots.getOrNull(slot) == null && !parkedState.activeCartIsEmpty
        parkedCartVM.tapSlot(slot)
        if (willPark) {
            snackbar.showToast(
                PharmToast.Info(
                    message = "พักตะกร้าไว้ช่อง ${slot + 1} แล้ว",
                    action = ToastAction("เปิดดู") { parkedCartVM.openSheet() },
                ),
            )
        }
    }

    val combinedError = sellState.error
        ?: checkoutState.error
        ?: drugState.error
        ?: customerState.error
        ?: voidState.error

    val dismissAllErrors: () -> Unit = {
        sellVM.dismissError()
        checkoutVM.dismissError()
        drugPickerVM.dismissError()
        customerPickerVM.dismissError()
        voidSaleVM.dismissError()
    }

    val onShortcutParkCart: () -> Unit = {
        if (!parkedState.activeCartIsEmpty) {
            val firstEmpty = parkedState.parkedSlots.indexOfFirst { it == null }
            if (firstEmpty >= 0) onTapParkSlot(firstEmpty) else parkedCartVM.openSheet()
        }
    }

    val onShortcutEscape: () -> Unit = {
        when {
            showShortcuts                          -> showShortcuts = false
            combinedError != null                  -> dismissAllErrors()
            parkedState.overwriteSlot != null      -> parkedCartVM.cancelOverwrite()
            parkedState.swapSlot != null           -> parkedCartVM.cancelSwap()
            sellState.showClearConfirm             -> sellVM.cancelClearCart()
            checkoutState.showSkipKyConfirm        -> checkoutVM.cancelSkipKy()
            checkoutState.oversellPending != null  -> checkoutVM.dismissOversell()
            checkoutState.kyCapturePending != null -> checkoutVM.dismissKyCapture()
            sellState.lineDiscountFor != null      -> sellVM.onCloseLineDiscount()
            sellState.cartDiscountSheetOpen        -> sellVM.onCloseCartDiscount()
            drugState.altUnitPickerFor != null     -> drugPickerVM.onCloseAltUnitPicker()
            voidState.sheetOpen                    -> voidSaleVM.closeSheet()
            customerState.open                     -> customerPickerVM.close()
            parkedState.sheetOpen                  -> parkedCartVM.closeSheet()
            sellState.receipt != null              -> checkoutVM.dismissReceipt()
        }
    }

    val sellShortcuts = arrayOf(
        PharmShortcut(
            key = Key.F1,
            label = "F1",
            action = { showShortcuts = true },
        ),
        PharmShortcut(
            key = Key.F2,
            label = "F2",
            action = { runCatching { searchFocus.requestFocus() } },
        ),
        PharmShortcut(
            key = Key.F3,
            label = "F3",
            action = customerPickerVM::open,
        ),
        PharmShortcut(
            key = Key.F4,
            label = "F4",
            action = sellVM::onOpenCartDiscount,
        ),
        PharmShortcut(
            key = Key.F6,
            label = "F6",
            action = onShortcutParkCart,
        ),
        PharmShortcut(
            key = Key.F8,
            label = "F8",
            action = parkedCartVM::openSheet,
        ),
        PharmShortcut(
            key = Key.F9,
            label = "F9",
            action = { if (checkoutState.canCheckout) checkoutVM.submit() },
        ),
        PharmShortcut(
            key = Key.Escape,
            label = "Esc",
            action = onShortcutEscape,
        ),
        PharmShortcut(
            key = Key.N,
            ctrl = true,
            label = "Ctrl+N",
            action = onShortcutParkCart,
        ),
        PharmShortcut(
            key = Key.P,
            ctrl = true,
            shift = true,
            label = "Ctrl+Shift+P",
            action = parkedCartVM::openSheet,
        ),
    )

    Surface(
        color = t.colors.bgPage,
        modifier = Modifier
            .fillMaxSize()
            .scanBarcodes(onScan = drugPickerVM::onScanBarcode)
            .pharmShortcuts(*sellShortcuts),
    ) {
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val isWide = maxWidth >= 720.dp
            if (isWide) {
                Row(modifier = Modifier.fillMaxSize()) {
                    DrugPickerColumn(
                        query = drugState.query,
                        onQueryChange = drugPickerVM::onQueryChange,
                        drugs = drugState.drugs,
                        visible = drugState.filteredDrugs,
                        loading = drugState.drugsLoading,
                        activeTier = sellState.activeTier,
                        onAdd = drugPickerVM::onTapDrug,
                        modifier = Modifier.weight(0.62f),
                        searchFocusRequester = searchFocus,
                    )
                    VerticalDivider(color = t.colors.divider)
                    SellCartPanel(
                        sellState = sellState,
                        canCheckout = checkoutState.canCheckout,
                        checkingOut = checkoutState.checkingOut,
                        onSetQty = sellVM::onSetQty,
                        onRemove = sellVM::onRemove,
                        onTapLineForDiscount = sellVM::onOpenLineDiscount,
                        onOpenCartDiscount = sellVM::onOpenCartDiscount,
                        onReceivedChange = sellVM::onReceivedChange,
                        onRequestClearCart = sellVM::requestClearCart,
                        onConfirmClearCart = sellVM::confirmClearCart,
                        onCancelClearCart = sellVM::cancelClearCart,
                        onSubmit = checkoutVM::submit,
                        parkedFilledCount = parkedState.filledCount,
                        onPickCustomer = customerPickerVM::open,
                        onClearCustomer = customerPickerVM::clear,
                        onOpenParkedSheet = parkedCartVM::openSheet,
                        modifier = Modifier.width(420.dp),
                    )

                    CartSlotRail(
                        slots = parkedState.parkedSlots,
                        onTapSlot = onTapParkSlot,
                    )
                }
            } else {
                Column(modifier = Modifier.fillMaxSize()) {
                    DrugPickerColumn(
                        query = drugState.query,
                        onQueryChange = drugPickerVM::onQueryChange,
                        drugs = drugState.drugs,
                        visible = drugState.filteredDrugs,
                        loading = drugState.drugsLoading,
                        activeTier = sellState.activeTier,
                        onAdd = drugPickerVM::onTapDrug,
                        modifier = Modifier.weight(1f).fillMaxWidth(),
                        searchFocusRequester = searchFocus,
                    )
                    CartFooterBar(
                        itemCount = sellState.cartItemCount,
                        total = sellState.total,
                        onClick = onOpenCart,
                        parkedFilledCount = parkedState.filledCount,
                    )
                }
            }

            drugState.altUnitPickerFor?.let { drug ->
                AltUnitPickerSheet(
                    drug = drug,
                    activeTier = sellState.activeTier,
                    onPick = drugPickerVM::onPickAltUnit,
                    onDismiss = drugPickerVM::onCloseAltUnitPicker,
                )
            }

            if (parkedState.sheetOpen) {
                ParkedCartsSheet(
                    slots = parkedState.parkedSlots,
                    canParkActiveCart = !parkedState.activeCartIsEmpty,
                    onTapSlot = onTapParkSlot,
                    onDiscardSlot = parkedCartVM::discard,
                    onRequestOverwrite = parkedCartVM::requestOverwrite,
                    onDismiss = parkedCartVM::closeSheet,
                )
            }
            parkedState.overwriteSlot?.let { slot ->
                ParkOverwriteDialog(
                    slotNumber = slot + 1,
                    onConfirm = parkedCartVM::confirmOverwrite,
                    onCancel = parkedCartVM::cancelOverwrite,
                )
            }
            parkedState.swapSlot?.let { slot ->
                SwapToParkedDialog(
                    slotNumber = slot + 1,
                    onConfirm = parkedCartVM::confirmSwap,
                    onCancel = parkedCartVM::cancelSwap,
                )
            }

            checkoutState.kyCapturePending?.let { required ->
                KyCaptureSheet(
                    required = required,
                    initial = sellState.kyInitialFields,
                    submitting = checkoutState.checkingOut,
                    onConfirm = checkoutVM::confirmKyCapture,
                    onSkip = checkoutVM::requestSkipKy,
                    onDismiss = checkoutVM::dismissKyCapture,
                )
            }

            if (checkoutState.showSkipKyConfirm) {
                SkipKyConfirmSheet(
                    onConfirm = checkoutVM::confirmSkipKy,
                    onDismiss = checkoutVM::cancelSkipKy,
                )
            }

            if (isWide) {
                if (customerState.open) {
                    CustomerPickerSheet(
                        customers = customerState.customers,
                        loading = customerState.loading,
                        onPick = customerPickerVM::pick,
                        onDismiss = customerPickerVM::close,
                    )
                }
                sellState.lineDiscountFor?.let { line ->
                    LineDiscountSheet(
                        line = line,
                        onApply = sellVM::onApplyLineDiscount,
                        onDismiss = sellVM::onCloseLineDiscount,
                    )
                }
                if (sellState.cartDiscountSheetOpen) {
                    CartDiscountSheet(
                        current = sellState.cartDiscount,
                        subtotal = sellState.subtotal,
                        onApply = sellVM::onApplyCartDiscount,
                        onDismiss = sellVM::onCloseCartDiscount,
                    )
                }
                checkoutState.oversellPending?.let { rows ->
                    OversellConfirmSheet(
                        shortfalls = rows,
                        onConfirm = checkoutVM::confirmOversell,
                        onDismiss = checkoutVM::dismissOversell,
                    )
                }
                sellState.receipt?.let { sale ->
                    val template = checkoutState.lastReceiptTemplate
                    if (template != null) {
                        ReceiptDialog(
                            template = template,
                            onDismiss = checkoutVM::dismissReceipt,
                            onVoid = sale.id.takeIf { it.isNotBlank() }
                                ?.let { { voidSaleVM.openSheet() } },
                            onPrint = { checkoutVM.printLastReceipt(sale) },
                        )
                    }
                    if (voidState.sheetOpen) {
                        VoidReasonSheet(
                            billNo = sale.billNo,
                            submitting = voidState.submitting,
                            onConfirm = { reason -> voidSaleVM.confirm(sale.id, reason) },
                            onDismiss = voidSaleVM::closeSheet,
                        )
                    }
                }
            }
        }
    }

    ErrorBottomSheet(
        message = combinedError,
        onDismiss = dismissAllErrors,
    )

    ShortcutLegend(open = showShortcuts, onClose = { showShortcuts = false })
}

@Composable
private fun SellCartPanel(
    sellState: SellUiState,
    canCheckout: Boolean,
    checkingOut: Boolean,
    onSetQty: (CartLineKey, Int) -> Unit,
    onRemove: (CartLineKey) -> Unit,
    onTapLineForDiscount: (CartLine) -> Unit,
    onOpenCartDiscount: () -> Unit,
    onReceivedChange: (String) -> Unit,
    onRequestClearCart: () -> Unit,
    onConfirmClearCart: () -> Unit,
    onCancelClearCart: () -> Unit,
    onSubmit: () -> Unit,
    parkedFilledCount: Int,
    onPickCustomer: () -> Unit,
    onClearCustomer: () -> Unit,
    onOpenParkedSheet: () -> Unit,
    modifier: Modifier = Modifier,
) {
    CartPanel(
        cart = sellState.cart,
        customer = sellState.customer,
        activeTier = sellState.activeTier,
        cartDiscount = sellState.cartDiscount,
        received = sellState.received,
        grossSubtotal = sellState.grossSubtotal,
        itemDiscountTotal = sellState.itemDiscountTotal,
        cartDiscountAmount = sellState.cartDiscountAmount,
        total = sellState.total,
        change = sellState.change,
        canCheckout = canCheckout,
        checkingOut = checkingOut,
        onSetQty = onSetQty,
        onRemove = onRemove,
        onTapLineForDiscount = onTapLineForDiscount,
        onPickCustomer = onPickCustomer,
        onClearCustomer = onClearCustomer,
        onOpenCartDiscount = onOpenCartDiscount,
        onReceivedChange = onReceivedChange,
        onSubmit = onSubmit,
        showClearConfirm = sellState.showClearConfirm,
        onRequestClearCart = onRequestClearCart,
        onConfirmClearCart = onConfirmClearCart,
        onCancelClearCart = onCancelClearCart,
        parkedFilledCount = parkedFilledCount,
        onOpenParkedSheet = onOpenParkedSheet,
        showShortcutHints = true,
        modifier = modifier,
    )
}
