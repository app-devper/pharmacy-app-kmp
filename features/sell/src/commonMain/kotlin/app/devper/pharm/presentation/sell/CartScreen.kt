package app.devper.pharm.presentation.sell
import app.devper.pharm.presentation.sell.flow.CheckoutViewModel
import app.devper.pharm.presentation.sell.flow.VoidSaleViewModel
import app.devper.pharm.presentation.sell.flow.ParkedCartViewModel
import app.devper.pharm.presentation.sell.flow.CustomerPickerViewModel
import app.devper.pharm.presentation.sell.flow.DrugPickerViewModel

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import app.devper.pharm.ui.common.LocalPharmSnackbar
import app.devper.pharm.ui.common.PharmToast
import app.devper.pharm.ui.common.ToastAction
import app.devper.pharm.presentation.sell.i18n.localizeSell
import app.devper.pharm.ui.components.ErrorBottomSheet
import app.devper.pharm.ui.i18n.pharmStrings
import app.devper.pharm.ui.components.SubPageBar
import app.devper.pharm.presentation.sell.components.AltUnitPickerSheet
import app.devper.pharm.presentation.sell.components.CartDiscountSheet
import app.devper.pharm.presentation.sell.components.CartPanel
import app.devper.pharm.presentation.sell.components.CartTabStrip
import app.devper.pharm.presentation.sell.components.CustomerPickerSheet
import app.devper.pharm.presentation.sell.components.KyCaptureSheet
import app.devper.pharm.presentation.sell.components.SkipKyConfirmSheet
import app.devper.pharm.presentation.sell.components.LineDiscountSheet
import app.devper.pharm.presentation.sell.components.OversellConfirmSheet
import app.devper.pharm.presentation.sell.components.ParkOverwriteDialog
import app.devper.pharm.presentation.sell.components.ParkedCartsSheet
import app.devper.pharm.presentation.sell.components.PaymentDialog
import app.devper.pharm.presentation.sell.components.ReceiptDialog
import app.devper.pharm.presentation.sell.components.VoidReasonSheet
import app.devper.pharm.ui.theme.pharmTokens
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun CartScreen(
    onBack: () -> Unit,
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
    val s = pharmStrings
    val snackbar = LocalPharmSnackbar.current
    val onTapParkSlot: (Int) -> Unit = { slot ->
        val willPark = parkedState.parkedSlots.getOrNull(slot) == null && !parkedState.activeCartIsEmpty
        parkedCartVM.tapSlot(slot)
        if (willPark) {
            snackbar.showToast(
                PharmToast.Info(
                    message = s.sellParkedToast(slot + 1),
                    action = ToastAction(s.sellOpenViewCta) { parkedCartVM.openSheet() },
                ),
            )
        }
    }

    Column(modifier = Modifier.fillMaxSize().background(t.colors.bgPage)) {
        SubPageBar(
            title = pharmStrings.sellCart,
            onBack = onBack,
        )
        CartTabStrip(
            slots = parkedState.parkedSlots,
            activeSlot = parkedState.activeSlot,
            onTapSlot = parkedCartVM::tapSlot,
        )
        Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
            CartPanel(
                cart = sellState.cart,
                customer = sellState.customer,
                activeTier = sellState.activeTier,
                cartDiscount = sellState.cartDiscount,
                grossSubtotal = sellState.grossSubtotal.amount,
                itemDiscountTotal = sellState.itemDiscountTotal.amount,
                cartDiscountAmount = sellState.cartDiscountAmount.amount,
                total = sellState.total.amount,
                canCheckout = checkoutState.canCheckout,
                checkingOut = checkoutState.checkingOut,
                onSetQty = sellVM::onSetQty,
                onRemove = sellVM::onRemove,
                onTapLineForDiscount = sellVM::onOpenLineDiscount,
                onPickCustomer = customerPickerVM::open,
                onClearCustomer = customerPickerVM::clear,
                onOpenCartDiscount = sellVM::onOpenCartDiscount,
                onOpenPayment = checkoutVM::openPayment,
                showClearConfirm = sellState.showClearConfirm,
                onRequestClearCart = sellVM::requestClearCart,
                onConfirmClearCart = sellVM::confirmClearCart,
                onCancelClearCart = sellVM::cancelClearCart,
                activeSlot = parkedState.activeSlot,
                parkedFilledCount = parkedState.filledCount,
                onOpenParkedSheet = parkedCartVM::openSheet,
                compact = true,
                kyCaptured = checkoutState.kyCaptured,
                kySkipAuto = sellState.settings.ky.skipAuto,
                onOpenKyPrecapture = checkoutVM::openKyPrecapture,
            )
        }
    }

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
            subtotal = sellState.subtotal.amount,
            onApply = sellVM::onApplyCartDiscount,
            onDismiss = sellVM::onCloseCartDiscount,
        )
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
    if (checkoutState.paymentOpen) {
        PaymentDialog(
            received = sellState.received,
            total = sellState.total.amount,
            checkingOut = checkoutState.checkingOut,
            onReceivedChange = sellVM::onReceivedChange,
            onSubmit = checkoutVM::submit,
            onSubmitExact = checkoutVM::submitExact,
            onDismiss = checkoutVM::closePayment,
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
                onDismiss = {
                    checkoutVM.dismissReceipt()

                    onBack()
                },
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

    val combinedError = sellState.errorState
        ?: checkoutState.errorState
        ?: drugState.errorState
        ?: customerState.errorState
        ?: voidState.errorState
    val combinedErrorText = combinedError?.localizeSell(pharmStrings)
    ErrorBottomSheet(
        message = combinedErrorText,
        onDismiss = {
            sellVM.dismissError()
            checkoutVM.dismissError()
            drugPickerVM.dismissError()
            customerPickerVM.dismissError()
            voidSaleVM.dismissError()
        },
    )
}
