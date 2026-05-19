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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import app.devper.pharm.ui.components.ErrorBottomSheet
import app.devper.pharm.presentation.sell.components.AltUnitPickerSheet
import app.devper.pharm.presentation.sell.components.CartDiscountSheet
import app.devper.pharm.presentation.sell.components.CartPanel
import app.devper.pharm.presentation.sell.components.CustomerPickerSheet
import app.devper.pharm.presentation.sell.components.KyCaptureSheet
import app.devper.pharm.presentation.sell.components.LineDiscountSheet
import app.devper.pharm.presentation.sell.components.OversellConfirmSheet
import app.devper.pharm.presentation.sell.components.ParkOverwriteDialog
import app.devper.pharm.presentation.sell.components.SwapToParkedDialog
import app.devper.pharm.presentation.sell.components.ParkedCartsSheet
import app.devper.pharm.presentation.sell.components.ReceiptDialog
import app.devper.pharm.presentation.sell.components.VoidReasonSheet
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
    val sellState by sellVM.state.collectAsState()
    val checkoutState by checkoutVM.state.collectAsState()
    val drugState by drugPickerVM.state.collectAsState()
    val customerState by customerPickerVM.state.collectAsState()
    val parkedState by parkedCartVM.state.collectAsState()
    val voidState by voidSaleVM.state.collectAsState()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "ตะกร้า",
                        style = MaterialTheme.typography.titleLarge,
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
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
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
                onClearCart = sellVM::onClearCart,
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
            onSkip = checkoutVM::skipKyCapture,
            onDismiss = checkoutVM::dismissKyCapture,
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
        ReceiptDialog(
            sale = sale,
            received = sellState.receivedNum,
            onDismiss = {
                checkoutVM.dismissReceipt()

                onBack()
            },
            onVoid = sale.id.takeIf { it.isNotBlank() }
                ?.let { { voidSaleVM.openSheet() } },
            onPrint = { checkoutVM.printLastReceipt(sale) },
        )
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
