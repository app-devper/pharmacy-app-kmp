package app.devper.pharm.presentation.sell.components

import app.devper.pharm.ui.components.PharmBreakpoint
import app.devper.pharm.ui.i18n.pharmStrings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import app.devper.pharm.domain.model.CartDiscount
import app.devper.pharm.domain.model.CartLine
import app.devper.pharm.domain.model.CartLineKey
import app.devper.pharm.domain.model.Customer
import app.devper.pharm.domain.model.KyRequired
import app.devper.pharm.domain.extension.calculateKyRequired
import app.devper.pharm.ui.common.ShortcutHint
import app.devper.pharm.ui.designsystem.PharmDivider
import app.devper.pharm.ui.designsystem.pharmBannerEnter
import app.devper.pharm.ui.designsystem.LocalReducedMotion
import app.devper.pharm.ui.designsystem.PharmAnimatedBaht
import app.devper.pharm.ui.designsystem.PharmButton
import app.devper.pharm.ui.designsystem.PharmButtonSize
import app.devper.pharm.ui.designsystem.PharmIcons
import app.devper.pharm.ui.designsystem.PharmIconButton
import app.devper.pharm.ui.designsystem.PharmButtonVariant
import app.devper.pharm.ui.designsystem.PharmModal
import app.devper.pharm.ui.designsystem.PharmModalSize
import app.devper.pharm.ui.theme.PharmText
import app.devper.pharm.ui.theme.pharmTokens
import app.devper.pharm.ui.common.pharmClickable
import app.devper.pharm.ui.designsystem.PharmCircularProgress

@Composable
fun CartPanel(
    cart: List<CartLine>,
    customer: Customer?,
    activeTier: String,
    cartDiscount: CartDiscount,
    grossSubtotal: Double,
    itemDiscountTotal: Double,
    cartDiscountAmount: Double,
    total: Double,
    canCheckout: Boolean,
    checkingOut: Boolean,
    onSetQty: (key: CartLineKey, displayQty: Int) -> Unit,
    onRemove: (key: CartLineKey) -> Unit,
    onTapLineForDiscount: (CartLine) -> Unit,
    onPickCustomer: () -> Unit,
    onClearCustomer: () -> Unit,
    onOpenCartDiscount: () -> Unit,
    onOpenPayment: () -> Unit,
    showClearConfirm: Boolean,
    onRequestClearCart: () -> Unit,
    onConfirmClearCart: () -> Unit,
    onCancelClearCart: () -> Unit,
    activeSlot: Int = 0,
    @Suppress("UNUSED_PARAMETER") parkedFilledCount: Int = 0,
    @Suppress("UNUSED_PARAMETER") onOpenParkedSheet: () -> Unit = {},
    showShortcutHints: Boolean = false,
    kyCaptured: Boolean = false,
    kyInvalidated: Boolean = false,
    kySkipAuto: Boolean = false,
    onOpenKyPrecapture: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val reducedMotion = LocalReducedMotion.current
    val t = pharmTokens
    val cartCount = cart.sumOf { it.qty }
    val hasItems = cart.isNotEmpty()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(t.colors.surface),
    ) {
        CartPanelHeader(
            activeSlot = activeSlot,
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

        val kyRequired = remember(cart) { cart.calculateKyRequired() }
        AnimatedVisibility(
            visible = !kyRequired.isEmpty,
            enter = pharmBannerEnter(),
            exit = ExitTransition.None,
        ) {
            CartComplianceBanner(required = kyRequired, captured = kyCaptured, invalidated = kyInvalidated, skipAuto = kySkipAuto, onClick = onOpenKyPrecapture)
        }

        CartSectionDivider()

        BoxWithConstraints(modifier = Modifier.weight(1f).fillMaxWidth()) {
            val narrowRows = maxWidth < PharmBreakpoint.Stack
            if (hasItems) {
                LazyColumn {
                    itemsIndexed(
                        cart,
                        key = { _, line -> "${line.drug.id}::${line.selectedUnit?.name.orEmpty()}" },
                    ) { index, line ->
                        Box(modifier = if (reducedMotion) Modifier else Modifier.animateItem()) {
                            if (index > 0) CartSectionDivider()
                            CartLineRow(
                                line = line,
                                onQtyChange = { displayQty -> onSetQty(line.key, displayQty) },
                                onRemove = { onRemove(line.key) },
                                onTapForDiscount = { onTapLineForDiscount(line) },
                                narrow = narrowRows,
                            )
                        }
                    }
                }
            } else {
                EmptyCart()
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

            CartPayButton(
                total = total,
                canCheckout = canCheckout,
                checkingOut = checkingOut,
                onOpenPayment = onOpenPayment,
                showShortcutHint = showShortcutHints,
            )
        }
    }
}

@Composable
private fun CartPanelHeader(
    activeSlot: Int,
    cartCount: Int,
    hasItems: Boolean,
    checkingOut: Boolean,
    showClearConfirm: Boolean,
    onRequestClearCart: () -> Unit,
    onConfirmClearCart: () -> Unit,
    onCancelClearCart: () -> Unit,
) {
    val t = pharmTokens
    val s = pharmStrings

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 12.dp, end = 4.dp, top = 4.dp, bottom = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text("${s.sellCart} #${activeSlot + 1}", style = PharmText.h3)
            Text(
                text = "· " + s.commonItemsCount(cartCount),
                style = PharmText.meta,
            )
        }
        val canClear = hasItems && !checkingOut
        PharmIconButton(
            contentDescription = s.sellClearCartCta,
            onClick = onRequestClearCart,
            enabled = canClear,
            modifier = Modifier.size(48.dp),
        ) {
            Icon(
                PharmIcons.Trash,
                contentDescription = null,
                tint = if (canClear) t.colors.dangerFg else t.colors.fgMuted,
                modifier = Modifier.size(18.dp),
            )
        }
    }

    PharmModal(
        open = showClearConfirm,
        onDismiss = onCancelClearCart,
        title = pharmStrings.sellRemoveCart,
        size = PharmModalSize.Sm,
        footer = {
            PharmButton(
                label = pharmStrings.commonCancel,
                onClick = onCancelClearCart,
                variant = PharmButtonVariant.Ghost,
                size = PharmButtonSize.Sm,
            )
            PharmButton(
                label = pharmStrings.sellClearCartCta,
                onClick = onConfirmClearCart,
                variant = PharmButtonVariant.Danger,
                size = PharmButtonSize.Sm,
            )
        },
    ) {
        Text(
            pharmStrings.sellClearCartBody(cartCount),
            style = PharmText.body,
        )
    }
}

@Composable
private fun CartPayButton(
    total: Double,
    canCheckout: Boolean,
    checkingOut: Boolean,
    onOpenPayment: () -> Unit,
    showShortcutHint: Boolean = false,
) {
    val t = pharmTokens
    Box(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
        PharmButton(
            onClick = onOpenPayment,
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
                            pharmStrings.sellPayment,
                            style = PharmText.buttonMd.copy(
                                color = t.colors.surface,
                                fontWeight = FontWeight.SemiBold,
                            ),
                        )
                    }
                    PharmAnimatedBaht(
                        value = total,
                        style = PharmText.total.copy(color = t.colors.surface),
                    )
                }
            }
        }
    }
}

@Composable
private fun CartSectionDivider() {
    PharmDivider()
}

@Composable
private fun CartComplianceBanner(required: KyRequired, captured: Boolean, invalidated: Boolean, skipAuto: Boolean, onClick: () -> Unit) {
    val t = pharmTokens
    val forms = buildList {
        if (required.needsKy10) add("10")
        if (required.needsKy11) add("11")
        if (required.needsKy12) add("12")
    }.joinToString(", ")
    val bg = when {
        skipAuto -> t.colors.dangerBg
        captured -> t.colors.successBg
        else -> t.colors.warningBg
    }
    val fg = when {
        skipAuto -> t.colors.dangerFg
        captured -> t.colors.successFg
        else -> t.colors.warningFg
    }
    val clickMod = if (skipAuto) Modifier else Modifier.pharmClickable(role = Role.Button, onClick = onClick)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 4.dp)
            .clip(t.shapes.md)
            .then(clickMod)
            .background(bg)
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .semantics(mergeDescendants = true) { liveRegion = LiveRegionMode.Polite },
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Icon(
            imageVector = PharmIcons.KyForms,
            contentDescription = null,
            tint = fg,
            modifier = Modifier.size(16.dp),
        )
        Column(verticalArrangement = Arrangement.spacedBy(1.dp)) {
            Text(
                text = pharmStrings.sellControlledKy(forms),
                style = PharmText.micro.copy(color = fg, fontWeight = FontWeight.SemiBold),
            )
            Text(
                text = when {
                    skipAuto -> pharmStrings.sellKySkipAutoOn
                    captured -> pharmStrings.sellKyPrecaptureDone
                    invalidated -> pharmStrings.sellKyPrecaptureInvalidated
                    else -> pharmStrings.sellKyPrecaptureNeeded
                },
                style = PharmText.bodySm.copy(color = t.colors.warningFg),
            )
        }
    }
}
