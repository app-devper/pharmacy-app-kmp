package app.devper.pharm.presentation.sell.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.ui.semantics.Role
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import app.devper.pharm.domain.model.CartDiscount
import app.devper.pharm.domain.model.CartLine
import app.devper.pharm.domain.model.CartLineKey
import app.devper.pharm.domain.model.Customer
import app.devper.pharm.ui.common.ShortcutHint
import app.devper.pharm.ui.designsystem.PharmButton
import app.devper.pharm.ui.designsystem.PharmButtonSize
import app.devper.pharm.ui.designsystem.PharmButtonVariant
import app.devper.pharm.ui.designsystem.PharmModal
import app.devper.pharm.ui.designsystem.PharmModalSize
import app.devper.pharm.ui.theme.PharmText
import app.devper.pharm.ui.theme.fmtBaht
import app.devper.pharm.ui.theme.pharmTokens
import app.devper.pharm.ui.designsystem.PharmCircularProgress

@Composable
fun CartPanel(
    cart: List<CartLine>,
    customer: Customer?,
    activeTier: String,
    cartDiscount: CartDiscount,
    received: String,
    grossSubtotal: Double,
    itemDiscountTotal: Double,
    cartDiscountAmount: Double,
    total: Double,
    change: Double,
    canCheckout: Boolean,
    checkingOut: Boolean,
    onSetQty: (key: CartLineKey, displayQty: Int) -> Unit,
    onRemove: (key: CartLineKey) -> Unit,
    onTapLineForDiscount: (CartLine) -> Unit,
    onPickCustomer: () -> Unit,
    onClearCustomer: () -> Unit,
    onOpenCartDiscount: () -> Unit,
    onReceivedChange: (String) -> Unit,
    onSubmit: () -> Unit,
    showClearConfirm: Boolean,
    onRequestClearCart: () -> Unit,
    onConfirmClearCart: () -> Unit,
    onCancelClearCart: () -> Unit,
    @Suppress("UNUSED_PARAMETER") parkedFilledCount: Int = 0,
    @Suppress("UNUSED_PARAMETER") onOpenParkedSheet: () -> Unit = {},
    compact: Boolean = false,
    showShortcutHints: Boolean = false,
    modifier: Modifier = Modifier,
) {
    val t = pharmTokens
    val cartCount = cart.sumOf { it.qty }
    val hasItems = cart.isNotEmpty()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(t.colors.surface),
    ) {
        CartPanelHeader(
            cartCount = cartCount,
            hasItems = hasItems,
            checkingOut = checkingOut,
            showClearConfirm = showClearConfirm,
            onRequestClearCart = onRequestClearCart,
            onConfirmClearCart = onConfirmClearCart,
            onCancelClearCart = onCancelClearCart,
        )
        CartSectionDivider()

        CartCustomerPill(
            customer = customer,
            activeTier = activeTier,
            onPick = onPickCustomer,
            onClear = onClearCustomer,
            showShortcutHint = showShortcutHints,
        )

        Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
            if (hasItems) {
                LazyColumn(contentPadding = PaddingValues(vertical = 4.dp)) {
                    items(
                        cart,
                        key = { line -> "${line.drug.id}::${line.selectedUnit?.name.orEmpty()}" },
                    ) { line ->
                        CartLineRow(
                            line = line,
                            onQtyChange = { displayQty -> onSetQty(line.key, displayQty) },
                            onRemove = { onRemove(line.key) },
                            onTapForDiscount = { onTapLineForDiscount(line) },
                        )
                    }
                }
            } else {
                EmptyCart(compact = compact)
            }
        }

        if (hasItems) {
            CartSectionDivider()

            CartTotalsBlock(
                grossSubtotal = grossSubtotal,
                itemDiscountTotal = itemDiscountTotal,
                cartDiscount = cartDiscount,
                cartDiscountAmount = cartDiscountAmount,
                total = total,
                onOpenCartDiscount = onOpenCartDiscount,
                showShortcutHint = showShortcutHints,
            )

            CartSectionDivider()
            CartCashReceivedRow(
                received = received,
                total = total,
                change = change,
                checkingOut = checkingOut,
                onReceivedChange = onReceivedChange,
            )

            CartCheckoutButton(
                total = total,
                canCheckout = canCheckout,
                checkingOut = checkingOut,
                onSubmit = onSubmit,
                showShortcutHint = showShortcutHints,
            )
        }
    }
}

@Composable
private fun CartPanelHeader(
    cartCount: Int,
    hasItems: Boolean,
    checkingOut: Boolean,
    showClearConfirm: Boolean,
    onRequestClearCart: () -> Unit,
    onConfirmClearCart: () -> Unit,
    onCancelClearCart: () -> Unit,
) {
    val t = pharmTokens

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text("ตะกร้า", style = PharmText.h3)
            Text(
                text = "· $cartCount รายการ",
                style = PharmText.meta,
            )
        }
        val canClear = hasItems && !checkingOut
        Box(
            modifier = Modifier
                .clip(t.shapes.sm)
                .clickable(role = Role.Button, onClick = onRequestClearCart, enabled = canClear)
                .defaultMinSize(minHeight = 44.dp)
                .padding(horizontal = 8.dp, vertical = 4.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                "ล้าง",
                style = PharmText.micro.copy(
                    color = if (canClear) t.colors.dangerFg else t.colors.fgMuted,
                ),
            )
        }
    }

    PharmModal(
        open = showClearConfirm,
        onDismiss = onCancelClearCart,
        title = "ลบรายการในตะกร้า?",
        size = PharmModalSize.Sm,
        footer = {
            PharmButton(
                label = "ยกเลิก",
                onClick = onCancelClearCart,
                variant = PharmButtonVariant.Ghost,
                size = PharmButtonSize.Sm,
            )
            PharmButton(
                label = "ล้าง",
                onClick = onConfirmClearCart,
                variant = PharmButtonVariant.Danger,
                size = PharmButtonSize.Sm,
            )
        },
    ) {
        Text(
            "ลบรายการในตะกร้าทั้งหมด $cartCount รายการ? การกระทำนี้ย้อนกลับไม่ได้",
            style = PharmText.body,
        )
    }
}

@Composable
private fun CartCheckoutButton(
    total: Double,
    canCheckout: Boolean,
    checkingOut: Boolean,
    onSubmit: () -> Unit,
    showShortcutHint: Boolean = false,
) {
    val t = pharmTokens
    Box(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
        PharmButton(
            onClick = onSubmit,
            modifier = Modifier.fillMaxWidth(),
            size = PharmButtonSize.Lg,
            enabled = canCheckout && !checkingOut,
        ) {
            if (checkingOut) {
                Box(modifier = Modifier.size(18.dp)) {
                    PharmCircularProgress(
                        color = t.colors.surface,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(16.dp),
                    )
                }
            } else {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        if (showShortcutHint) {
                            ShortcutHint(label = "F9")
                        }
                        Text(
                            "ออกใบเสร็จ",
                            style = PharmText.buttonMd.copy(
                                color = t.colors.surface,
                                fontWeight = FontWeight.SemiBold,
                            ),
                        )
                    }
                    Text(
                        fmtBaht(total),
                        style = PharmText.total.copy(color = t.colors.surface),
                    )
                }
            }
        }
    }
}

@Composable
private fun CartSectionDivider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(pharmTokens.colors.divider),
    )
}
