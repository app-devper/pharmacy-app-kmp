package app.devper.pharm.presentation.sell
import app.devper.pharm.presentation.sell.sibling.CheckoutViewModel
import app.devper.pharm.presentation.sell.sibling.VoidSaleViewModel
import app.devper.pharm.presentation.sell.sibling.ParkedCartViewModel
import app.devper.pharm.presentation.sell.sibling.CustomerPickerViewModel
import app.devper.pharm.presentation.sell.sibling.DrugPickerViewModel

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import app.devper.pharm.ui.common.LocalPharmSnackbar
import app.devper.pharm.ui.common.PharmToast
import app.devper.pharm.ui.common.ToastAction
import app.devper.pharm.ui.components.ErrorBottomSheet
import app.devper.pharm.presentation.sell.components.AltUnitPickerSheet
import app.devper.pharm.presentation.sell.components.CartDiscountSheet
import app.devper.pharm.presentation.sell.components.CartPanel
import app.devper.pharm.presentation.sell.components.CustomerPickerSheet
import app.devper.pharm.presentation.sell.components.KyCaptureSheet
import app.devper.pharm.presentation.sell.components.SkipKyConfirmSheet
import app.devper.pharm.presentation.sell.components.LineDiscountSheet
import app.devper.pharm.presentation.sell.components.OversellConfirmSheet
import app.devper.pharm.presentation.sell.components.ParkOverwriteDialog
import app.devper.pharm.presentation.sell.components.SwapToParkedDialog
import app.devper.pharm.presentation.sell.components.ParkedCartsSheet
import app.devper.pharm.presentation.sell.components.ReceiptDialog
import app.devper.pharm.presentation.sell.components.VoidReasonSheet
import app.devper.pharm.ui.theme.PharmText
import app.devper.pharm.ui.theme.pharmTokens
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
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

    Scaffold(
        containerColor = t.colors.bgPage,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "ตะกร้า",
                        style = PharmText.h1,
                        fontWeight = FontWeight.SemiBold,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                            contentDescription = "ย้อนกลับ",
                            modifier = Modifier.size(20.dp),
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = t.colors.surface,
                    titleContentColor = t.colors.fg1,
                    navigationIconContentColor = t.colors.fg1,
                ),
            )
        },
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
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
                canCheckout = checkoutState.canCheckout,
                checkingOut = checkoutState.checkingOut,
                onSetQty = sellVM::onSetQty,
                onRemove = sellVM::onRemove,
                onTapLineForDiscount = sellVM::onOpenLineDiscount,
                onPickCustomer = customerPickerVM::open,
                onClearCustomer = customerPickerVM::clear,
                onOpenCartDiscount = sellVM::onOpenCartDiscount,
                onReceivedChange = sellVM::onReceivedChange,
                onSubmit = checkoutVM::submit,
                showClearConfirm = sellState.showClearConfirm,
                onRequestClearCart = sellVM::requestClearCart,
                onConfirmClearCart = sellVM::confirmClearCart,
                onCancelClearCart = sellVM::cancelClearCart,
                parkedFilledCount = parkedState.filledCount,
                onOpenParkedSheet = parkedCartVM::openSheet,
                compact = true,
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
            subtotal = sellState.subtotal,
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
