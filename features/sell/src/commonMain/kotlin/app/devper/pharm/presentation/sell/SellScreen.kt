package app.devper.pharm.presentation.sell
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import app.devper.pharm.ui.designsystem.PharmVerticalDivider
import app.devper.pharm.ui.components.PharmBreakpoint
import app.devper.pharm.presentation.sell.flow.CheckoutViewModel
import app.devper.pharm.presentation.sell.flow.VoidSaleViewModel
import app.devper.pharm.presentation.sell.flow.ParkedCartViewModel
import app.devper.pharm.presentation.sell.flow.CustomerPickerViewModel
import app.devper.pharm.presentation.sell.flow.DrugPickerViewModel

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.unit.dp
import app.devper.pharm.ui.common.pharmShortcuts
import app.devper.pharm.presentation.sell.i18n.localizeSell
import app.devper.pharm.ui.components.ErrorBottomSheet
import app.devper.pharm.ui.i18n.pharmStrings
import app.devper.pharm.ui.scanner.scanBarcodes
import app.devper.pharm.presentation.sell.components.CartFooterBar
import app.devper.pharm.presentation.sell.components.CartSlotRail
import app.devper.pharm.presentation.sell.components.CartTabStrip
import app.devper.pharm.presentation.sell.components.DrugPickerColumn
import app.devper.pharm.presentation.sell.components.ShortcutLegend
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
    var addedDrugName by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        drugPickerVM.added.collect { name ->
            addedDrugName = name
        }
    }
    val onTapParkSlot: (Int) -> Unit = { slot ->
        parkedCartVM.tapSlot(slot)
    }

    val combinedError = sellState.errorState
        ?: checkoutState.errorState
        ?: drugState.errorState
        ?: customerState.errorState
        ?: voidState.errorState
    val combinedErrorText = combinedError?.localizeSell(pharmStrings)

    val dismissAllErrors: () -> Unit = {
        sellVM.dismissError()
        checkoutVM.dismissError()
        drugPickerVM.dismissError()
        customerPickerVM.dismissError()
        voidSaleVM.dismissError()
    }

    val sellShortcuts = rememberSellShortcuts(
        sellVM = sellVM,
        checkoutVM = checkoutVM,
        drugPickerVM = drugPickerVM,
        customerPickerVM = customerPickerVM,
        parkedCartVM = parkedCartVM,
        voidSaleVM = voidSaleVM,
        sellState = sellState,
        checkoutState = checkoutState,
        drugState = drugState,
        customerState = customerState,
        parkedState = parkedState,
        voidState = voidState,
        shortcutsVisible = showShortcuts,
        onShowShortcuts = { showShortcuts = true },
        onHideShortcuts = { showShortcuts = false },
        hasError = combinedError != null,
        onDismissAllErrors = dismissAllErrors,
        searchFocus = searchFocus,
    )

    val overlayCallbacks = SellOverlayCallbacks(
        onTapParkSlot = onTapParkSlot,
        onPickAltUnit = drugPickerVM::onPickAltUnit,
        onCloseAltUnitPicker = drugPickerVM::onCloseAltUnitPicker,
        onDiscardParkedSlot = parkedCartVM::discard,
        onRequestOverwriteSlot = parkedCartVM::requestOverwrite,
        onCloseParkedSheet = parkedCartVM::closeSheet,
        onConfirmOverwrite = parkedCartVM::confirmOverwrite,
        onCancelOverwrite = parkedCartVM::cancelOverwrite,
        onReceivedChange = sellVM::onReceivedChange,
        onSubmitPayment = checkoutVM::submit,
        onSubmitExactPayment = checkoutVM::submitExact,
        onClosePayment = checkoutVM::closePayment,
        onConfirmKyCapture = checkoutVM::confirmKyCapture,
        onConfirmKyPrecapture = checkoutVM::confirmKyPrecapture,
        onDismissKyPrecapture = checkoutVM::dismissKyPrecapture,
        onRequestSkipKy = checkoutVM::requestSkipKy,
        onDismissKyCapture = checkoutVM::dismissKyCapture,
        onConfirmSkipKy = checkoutVM::confirmSkipKy,
        onCancelSkipKy = checkoutVM::cancelSkipKy,
        onPickCustomer = customerPickerVM::pick,
        onCloseCustomerPicker = customerPickerVM::close,
        onApplyLineDiscount = sellVM::onApplyLineDiscount,
        onCloseLineDiscount = sellVM::onCloseLineDiscount,
        onApplyCartDiscount = sellVM::onApplyCartDiscount,
        onCloseCartDiscount = sellVM::onCloseCartDiscount,
        onConfirmOversell = checkoutVM::confirmOversell,
        onDismissOversell = checkoutVM::dismissOversell,
        onDismissReceipt = checkoutVM::dismissReceipt,
        onOpenVoidSheet = voidSaleVM::openSheet,
        onPrintReceipt = { sale -> checkoutVM.printLastReceipt(sale) },
        onConfirmVoid = { saleId, reason -> voidSaleVM.confirm(saleId, reason) },
        onCloseVoidSheet = voidSaleVM::closeSheet,
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(t.colors.bgPage)
            .scanBarcodes(onScan = drugPickerVM::onScanBarcode)
            .pharmShortcuts(*sellShortcuts),
    ) {
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val isWide = maxWidth >= PharmBreakpoint.Medium
            val showRail = maxWidth >= PharmBreakpoint.Expanded
            val cartWidth = if (showRail) 400.dp else maxWidth * 0.45f
            if (isWide) {
                Row(modifier = Modifier.fillMaxSize()) {
                    DrugPickerColumn(
                        query = drugState.query,
                        onQueryChange = drugPickerVM::onQueryChange,
                        drugs = drugState.drugs,
                        visible = drugState.filteredDrugs,
                        loading = drugState.drugsLoading,
                        activeTier = sellState.activeTier,
                        addedDrugName = addedDrugName,
                        onAddedDrugMessageDismiss = { addedDrugName = null },
                        onAdd = drugPickerVM::onTapDrug,
                        modifier = Modifier.weight(1f),
                        searchFocusRequester = searchFocus,
                    )
                    PharmVerticalDivider()
                    Column(modifier = Modifier.width(cartWidth)) {
                        if (!showRail) {
                            CartTabStrip(
                                slots = parkedState.parkedSlots,
                                activeSlot = parkedState.activeSlot,
                                onTapSlot = onTapParkSlot,
                            )
                        }
                        SellCartPanel(
                            sellState = sellState,
                            canCheckout = checkoutState.canCheckout,
                            checkingOut = checkoutState.checkingOut,
                            kyCaptured = checkoutState.kyCaptured,
                            kyInvalidated = checkoutState.kyPrecaptureInvalidated,
                            kySkipAuto = sellState.settings.ky.skipAuto,
                            onOpenKyPrecapture = checkoutVM::openKyPrecapture,
                            onSetQty = sellVM::onSetQty,
                            onRemove = sellVM::onRemove,
                            onTapLineForDiscount = sellVM::onOpenLineDiscount,
                            onOpenCartDiscount = sellVM::onOpenCartDiscount,
                            onRequestClearCart = sellVM::requestClearCart,
                            onConfirmClearCart = sellVM::confirmClearCart,
                            onCancelClearCart = sellVM::cancelClearCart,
                            onOpenPayment = checkoutVM::openPayment,
                            activeSlot = parkedState.activeSlot,
                            parkedFilledCount = parkedState.filledCount,
                            onPickCustomer = customerPickerVM::open,
                            onClearCustomer = customerPickerVM::clear,
                            onOpenParkedSheet = parkedCartVM::openSheet,
                            modifier = Modifier.weight(1f).fillMaxWidth(),
                        )
                    }

                    if (showRail) {
                        CartSlotRail(
                            slots = parkedState.parkedSlots,
                            selectedSlot = parkedState.activeSlot,
                            onTapSlot = onTapParkSlot,
                        )
                    }
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
                        addedDrugName = addedDrugName,
                        onAddedDrugMessageDismiss = { addedDrugName = null },
                        onAdd = drugPickerVM::onTapDrug,
                        modifier = Modifier.weight(1f).fillMaxWidth(),
                        searchFocusRequester = searchFocus,
                    )
                    CartFooterBar(
                        itemCount = sellState.cartItemCount,
                        total = sellState.total.amount,
                        onClick = onOpenCart,
                        parkedFilledCount = parkedState.filledCount,
                    )
                }
            }

            SellOverlays(
                drugState = drugState,
                sellState = sellState,
                checkoutState = checkoutState,
                parkedState = parkedState,
                customerState = customerState,
                voidState = voidState,
                isWide = isWide,
                callbacks = overlayCallbacks,
            )
        }
    }

    ErrorBottomSheet(
        message = combinedErrorText,
        onDismiss = dismissAllErrors,
    )

    ShortcutLegend(open = showShortcuts, onClose = { showShortcuts = false })
}
