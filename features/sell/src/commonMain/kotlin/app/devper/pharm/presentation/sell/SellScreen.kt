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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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
import app.devper.pharm.presentation.sell.components.VoidReasonSheet
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
    val sellState by sellVM.state.collectAsState()
    val checkoutState by checkoutVM.state.collectAsState()
    val drugState by drugPickerVM.state.collectAsState()
    val customerState by customerPickerVM.state.collectAsState()
    val parkedState by parkedCartVM.state.collectAsState()
    val voidState by voidSaleVM.state.collectAsState()

    Surface(
        color = MaterialTheme.colorScheme.background,
        modifier = Modifier
            .fillMaxSize()

            .scanBarcodes(onScan = drugPickerVM::onScanBarcode),
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
                    )
                    VerticalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                    SellCartPanel(
                        sellState = sellState,
                        canCheckout = checkoutState.canCheckout,
                        checkingOut = checkoutState.checkingOut,
                        sellVM = sellVM,
                        onSubmit = checkoutVM::submit,
                        parkedFilledCount = parkedState.filledCount,
                        onPickCustomer = customerPickerVM::open,
                        onClearCustomer = customerPickerVM::clear,
                        onOpenParkedSheet = parkedCartVM::openSheet,
                        modifier = Modifier.width(400.dp),
                    )

                    CartSlotRail(
                        slots = parkedState.parkedSlots,
                        onTapSlot = parkedCartVM::tapSlot,
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
                    onTapSlot = parkedCartVM::tapSlot,
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

    val combinedError = sellState.error
        ?: checkoutState.error
        ?: drugState.error
        ?: customerState.error
        ?: voidState.error
    ErrorBottomSheet(
        message = combinedError,
        onDismiss = {
            sellVM.dismissError()
            checkoutVM.dismissError()
            drugPickerVM.dismissError()
            customerPickerVM.dismissError()
            voidSaleVM.dismissError()
        },
    )
}

@Composable
private fun SellCartPanel(
    sellState: SellUiState,
    canCheckout: Boolean,
    checkingOut: Boolean,
    sellVM: SellViewModel,
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
        onSetQty = sellVM::onSetQty,
        onRemove = sellVM::onRemove,
        onTapLineForDiscount = sellVM::onOpenLineDiscount,
        onPickCustomer = onPickCustomer,
        onClearCustomer = onClearCustomer,
        onOpenCartDiscount = sellVM::onOpenCartDiscount,
        onReceivedChange = sellVM::onReceivedChange,
        onSubmit = onSubmit,
        showClearConfirm = sellState.showClearConfirm,
        onRequestClearCart = sellVM::requestClearCart,
        onConfirmClearCart = sellVM::confirmClearCart,
        onCancelClearCart = sellVM::cancelClearCart,
        parkedFilledCount = parkedFilledCount,
        onOpenParkedSheet = onOpenParkedSheet,
        modifier = modifier,
    )
}
