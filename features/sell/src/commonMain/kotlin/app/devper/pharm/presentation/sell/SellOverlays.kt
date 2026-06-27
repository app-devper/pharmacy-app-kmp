package app.devper.pharm.presentation.sell

import androidx.compose.runtime.Composable
import app.devper.pharm.common.print.ReceiptTemplate
import app.devper.pharm.domain.model.AltUnit
import app.devper.pharm.domain.model.CartDiscount
import app.devper.pharm.domain.model.CartLineKey
import app.devper.pharm.domain.model.Customer
import app.devper.pharm.domain.model.KyCaptureFields
import app.devper.pharm.domain.model.Sale
import app.devper.pharm.presentation.sell.components.AltUnitPickerSheet
import app.devper.pharm.presentation.sell.components.CartDiscountSheet
import app.devper.pharm.presentation.sell.components.CustomerPickerSheet
import app.devper.pharm.presentation.sell.components.KyCaptureSheet
import app.devper.pharm.presentation.sell.components.LineDiscountSheet
import app.devper.pharm.presentation.sell.components.OversellConfirmSheet
import app.devper.pharm.presentation.sell.components.ParkOverwriteDialog
import app.devper.pharm.presentation.sell.components.ParkedCartsSheet
import app.devper.pharm.presentation.sell.components.PaymentDialog
import app.devper.pharm.presentation.sell.components.ReceiptDialog
import app.devper.pharm.presentation.sell.components.SkipKyConfirmSheet
import app.devper.pharm.presentation.sell.components.VoidReasonSheet
import app.devper.pharm.presentation.sell.flow.CheckoutUiState
import app.devper.pharm.presentation.sell.flow.CustomerPickerUiState
import app.devper.pharm.presentation.sell.flow.DrugPickerUiState
import app.devper.pharm.presentation.sell.flow.ParkedCartUiState
import app.devper.pharm.presentation.sell.flow.VoidSaleUiState

data class SellOverlayCallbacks(
    val onTapParkSlot: (Int) -> Unit = {},
    val onPickAltUnit: (AltUnit?) -> Unit = {},
    val onCloseAltUnitPicker: () -> Unit = {},
    val onDiscardParkedSlot: (Int) -> Unit = {},
    val onRequestOverwriteSlot: (Int) -> Unit = {},
    val onCloseParkedSheet: () -> Unit = {},
    val onConfirmOverwrite: () -> Unit = {},
    val onCancelOverwrite: () -> Unit = {},
    val onReceivedChange: (String) -> Unit = {},
    val onSubmitPayment: () -> Unit = {},
    val onSubmitExactPayment: () -> Unit = {},
    val onClosePayment: () -> Unit = {},
    val onConfirmKyCapture: (KyCaptureFields) -> Unit = {},
    val onRequestSkipKy: () -> Unit = {},
    val onDismissKyCapture: () -> Unit = {},
    val onConfirmSkipKy: () -> Unit = {},
    val onCancelSkipKy: () -> Unit = {},
    val onPickCustomer: (Customer) -> Unit = {},
    val onCloseCustomerPicker: () -> Unit = {},
    val onApplyLineDiscount: (CartLineKey, Double) -> Unit = { _, _ -> },
    val onCloseLineDiscount: () -> Unit = {},
    val onApplyCartDiscount: (CartDiscount) -> Unit = {},
    val onCloseCartDiscount: () -> Unit = {},
    val onConfirmOversell: () -> Unit = {},
    val onDismissOversell: () -> Unit = {},
    val onDismissReceipt: () -> Unit = {},
    val onOpenVoidSheet: () -> Unit = {},
    val onPrintReceipt: (Sale) -> Unit = {},
    val onConfirmVoid: (String, String) -> Unit = { _, _ -> },
    val onCloseVoidSheet: () -> Unit = {},
)

@Composable
internal fun SellOverlays(
    drugState: DrugPickerUiState,
    sellState: SellUiState,
    checkoutState: CheckoutUiState,
    parkedState: ParkedCartUiState,
    customerState: CustomerPickerUiState,
    voidState: VoidSaleUiState,
    isWide: Boolean,
    callbacks: SellOverlayCallbacks,
) {
    drugState.altUnitPickerFor?.let { drug ->
        AltUnitPickerSheet(
            drug = drug,
            activeTier = sellState.activeTier,
            onPick = callbacks.onPickAltUnit,
            onDismiss = callbacks.onCloseAltUnitPicker,
        )
    }

    if (parkedState.sheetOpen) {
        ParkedCartsSheet(
            slots = parkedState.parkedSlots,
            canParkActiveCart = !parkedState.activeCartIsEmpty,
            onTapSlot = callbacks.onTapParkSlot,
            onDiscardSlot = callbacks.onDiscardParkedSlot,
            onRequestOverwrite = callbacks.onRequestOverwriteSlot,
            onDismiss = callbacks.onCloseParkedSheet,
        )
    }

    parkedState.overwriteSlot?.let { slot ->
        ParkOverwriteDialog(
            slotNumber = slot + 1,
            onConfirm = callbacks.onConfirmOverwrite,
            onCancel = callbacks.onCancelOverwrite,
        )
    }

    if (checkoutState.paymentOpen) {
        PaymentDialog(
            received = sellState.received,
            total = sellState.total.amount,
            checkingOut = checkoutState.checkingOut,
            onReceivedChange = callbacks.onReceivedChange,
            onSubmit = callbacks.onSubmitPayment,
            onSubmitExact = callbacks.onSubmitExactPayment,
            onDismiss = callbacks.onClosePayment,
        )
    }

    checkoutState.kyCapturePending?.let { required ->
        KyCaptureSheet(
            required = required,
            initial = sellState.kyInitialFields,
            submitting = checkoutState.checkingOut,
            onConfirm = callbacks.onConfirmKyCapture,
            onSkip = callbacks.onRequestSkipKy,
            onDismiss = callbacks.onDismissKyCapture,
        )
    }

    if (checkoutState.showSkipKyConfirm) {
        SkipKyConfirmSheet(
            onConfirm = callbacks.onConfirmSkipKy,
            onDismiss = callbacks.onCancelSkipKy,
        )
    }

    if (isWide) {
        if (customerState.open) {
            CustomerPickerSheet(
                customers = customerState.customers,
                loading = customerState.loading,
                onPick = callbacks.onPickCustomer,
                onDismiss = callbacks.onCloseCustomerPicker,
            )
        }

        sellState.lineDiscountFor?.let { line ->
            LineDiscountSheet(
                line = line,
                onApply = callbacks.onApplyLineDiscount,
                onDismiss = callbacks.onCloseLineDiscount,
            )
        }

        if (sellState.cartDiscountSheetOpen) {
            CartDiscountSheet(
                current = sellState.cartDiscount,
                subtotal = sellState.subtotal.amount,
                onApply = callbacks.onApplyCartDiscount,
                onDismiss = callbacks.onCloseCartDiscount,
            )
        }

        checkoutState.oversellPending?.let { rows ->
            OversellConfirmSheet(
                shortfalls = rows,
                onConfirm = callbacks.onConfirmOversell,
                onDismiss = callbacks.onDismissOversell,
            )
        }

        sellState.receipt?.let { sale ->
            val template = checkoutState.lastReceiptTemplate
            if (template != null) {
                ReceiptDialog(
                    template = template,
                    onDismiss = callbacks.onDismissReceipt,
                    onVoid = sale.id.takeIf { it.isNotBlank() }?.let { { callbacks.onOpenVoidSheet() } },
                    onPrint = { callbacks.onPrintReceipt(sale) },
                )
            }
            if (voidState.sheetOpen) {
                VoidReasonSheet(
                    billNo = sale.billNo,
                    submitting = voidState.submitting,
                    onConfirm = { reason -> callbacks.onConfirmVoid(sale.id, reason) },
                    onDismiss = callbacks.onCloseVoidSheet,
                )
            }
        }
    }
}
