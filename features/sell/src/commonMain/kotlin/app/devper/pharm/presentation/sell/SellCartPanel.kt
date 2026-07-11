package app.devper.pharm.presentation.sell

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import app.devper.pharm.domain.model.CartLine
import app.devper.pharm.domain.model.CartLineKey
import app.devper.pharm.presentation.sell.components.CartPanel

@Composable
internal fun SellCartPanel(
    sellState: SellUiState,
    canCheckout: Boolean,
    checkingOut: Boolean,
    onSetQty: (CartLineKey, Int) -> Unit,
    onRemove: (CartLineKey) -> Unit,
    onTapLineForDiscount: (CartLine) -> Unit,
    onOpenCartDiscount: () -> Unit,
    onRequestClearCart: () -> Unit,
    onConfirmClearCart: () -> Unit,
    onCancelClearCart: () -> Unit,
    onOpenPayment: () -> Unit,
    activeSlot: Int = 0,
    parkedFilledCount: Int,
    onPickCustomer: () -> Unit,
    onClearCustomer: () -> Unit,
    onOpenParkedSheet: () -> Unit,
    kyCaptured: Boolean = false,
    kySkipAuto: Boolean = false,
    onOpenKyPrecapture: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    CartPanel(
        cart = sellState.cart,
        customer = sellState.customer,
        activeTier = sellState.activeTier,
        cartDiscount = sellState.cartDiscount,
        grossSubtotal = sellState.grossSubtotal.amount,
        itemDiscountTotal = sellState.itemDiscountTotal.amount,
        cartDiscountAmount = sellState.cartDiscountAmount.amount,
        total = sellState.total.amount,
        canCheckout = canCheckout,
        checkingOut = checkingOut,
        onSetQty = onSetQty,
        onRemove = onRemove,
        onTapLineForDiscount = onTapLineForDiscount,
        onPickCustomer = onPickCustomer,
        onClearCustomer = onClearCustomer,
        onOpenCartDiscount = onOpenCartDiscount,
        onOpenPayment = onOpenPayment,
        showClearConfirm = sellState.showClearConfirm,
        onRequestClearCart = onRequestClearCart,
        onConfirmClearCart = onConfirmClearCart,
        onCancelClearCart = onCancelClearCart,
        activeSlot = activeSlot,
        parkedFilledCount = parkedFilledCount,
        onOpenParkedSheet = onOpenParkedSheet,
        showShortcutHints = true,
        kyCaptured = kyCaptured,
        kySkipAuto = kySkipAuto,
        onOpenKyPrecapture = onOpenKyPrecapture,
        modifier = modifier,
    )
}
