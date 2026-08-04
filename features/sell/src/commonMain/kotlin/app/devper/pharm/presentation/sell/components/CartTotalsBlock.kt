package app.devper.pharm.presentation.sell.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.devper.pharm.domain.model.CartDiscount
import app.devper.pharm.ui.common.ShortcutHint
import app.devper.pharm.ui.theme.PharmText
import app.devper.pharm.ui.theme.PharmacyTheme
import app.devper.pharm.ui.theme.fmtBaht
import app.devper.pharm.ui.theme.pharmTokens
import app.devper.pharm.ui.common.pharmClickable
import app.devper.pharm.ui.i18n.pharmStrings
import app.devper.pharm.ui.designsystem.PharmDivider
import app.devper.pharm.ui.designsystem.PharmIcons

@Composable
fun CartTotalsBlock(
    grossSubtotal: Double,
    itemDiscountTotal: Double,
    cartDiscount: CartDiscount,
    cartDiscountAmount: Double,
    total: Double,
    onOpenCartDiscount: () -> Unit,
    showShortcutHint: Boolean = false,
) {
    val t = pharmTokens
    val s = pharmStrings

    val hasDiscounts = itemDiscountTotal > 0.0 || cartDiscountAmount > 0.0

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(s.sellTotal, style = PharmText.bodySm.copy(color = t.colors.fg2), modifier = Modifier.weight(1f))
            Text(fmtBaht(grossSubtotal), style = PharmText.bodySm.copy(color = t.colors.fg2))
        }

        PharmDivider()

        if (hasDiscounts) {
            Column(modifier = Modifier.fillMaxWidth()) {
                if (itemDiscountTotal > 0.0) {
                    CartTotalsRow(s.sellDiscountLine, "−${fmtBaht(itemDiscountTotal)}", color = t.colors.discount)
                }
                if (cartDiscountAmount > 0.0) {
                    CartTotalsRow(
                        label = cartDiscountLabel(cartDiscount),
                        value = "−${fmtBaht(cartDiscountAmount)}",
                        color = t.colors.discount,
                        onClick = onOpenCartDiscount,
                    )
                }
            }
        } else {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .pharmClickable(role = Role.Button, onClick = onOpenCartDiscount)
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                if (showShortcutHint) ShortcutHint(label = "F4")
                Text(
                    text = s.sellAddDiscount,
                    style = PharmText.bodySm.copy(color = t.colors.fg2),
                    modifier = Modifier.weight(1f),
                )
                Icon(
                    PharmIcons.ChevronRight,
                    contentDescription = null,
                    tint = t.colors.accent,
                    modifier = Modifier.size(20.dp),
                )
            }
        }
    }
}

@Composable
private fun cartDiscountLabel(cartDiscount: CartDiscount): String = when (cartDiscount) {
    is CartDiscount.None -> pharmStrings.sellCartDiscountShort
    is CartDiscount.Flat -> pharmStrings.sellCartDiscountShort
    is CartDiscount.Percent -> pharmStrings.sellCartDiscountPercentLabel(cartDiscount.percent.toInt())
}

@Composable
private fun CartTotalsRow(label: String, value: String, color: Color, onClick: (() -> Unit)? = null) {
    val t = pharmTokens
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.pharmClickable(role = Role.Button, onClick = onClick) else Modifier)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(label, style = PharmText.bodySm.copy(color = t.colors.fg2), modifier = Modifier.weight(1f))
        Text(value, style = PharmText.bodySm.copy(color = color))
    }
}

@Preview
@Composable
private fun CartTotalsBlock_NoDiscount_Preview() {
    PharmacyTheme {
        CartTotalsBlock(
            grossSubtotal = 450.0,
            itemDiscountTotal = 0.0,
            cartDiscount = CartDiscount.None,
            cartDiscountAmount = 0.0,
            total = 450.0,
            onOpenCartDiscount = {},
        )
    }
}

@Preview
@Composable
private fun CartTotalsBlock_WithDiscounts_Preview() {
    PharmacyTheme {
        CartTotalsBlock(
            grossSubtotal = 500.0,
            itemDiscountTotal = 30.0,
            cartDiscount = CartDiscount.Percent(percent = 5.0),
            cartDiscountAmount = 23.5,
            total = 446.5,
            onOpenCartDiscount = {},
        )
    }
}
