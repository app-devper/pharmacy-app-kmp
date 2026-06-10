package app.devper.pharm.presentation.sell.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.devper.pharm.domain.model.CartDiscount
import app.devper.pharm.ui.common.ShortcutHint
import app.devper.pharm.ui.theme.PharmText
import app.devper.pharm.ui.theme.PharmacyTheme
import app.devper.pharm.ui.theme.fmtBaht
import app.devper.pharm.ui.theme.pharmTokens
import app.devper.pharm.ui.i18n.pharmStrings

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
    val hasDiscount = itemDiscountTotal > 0.0 || cartDiscountAmount > 0.0
    var expanded by remember { mutableStateOf(false) }
    val open = expanded || hasDiscount

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = if (open) pharmStrings.sellHideSubtotal else pharmStrings.sellShowSubtotal,
                style = PharmText.micro.copy(color = t.colors.fg3),
                modifier = Modifier
                    .weight(1f)
                    .clickable(enabled = !hasDiscount) { expanded = !expanded }
                    .padding(vertical = 4.dp),
            )
            Row(
                modifier = Modifier
                    .clickable(role = Role.Button, onClick = onOpenCartDiscount)
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                if (showShortcutHint && cartDiscountAmount <= 0.0) {
                    ShortcutHint(label = "F4")
                }
                Text(
                    text = if (cartDiscountAmount > 0.0) {
                        "${cartDiscountLabel(cartDiscount)} −${fmtBaht(cartDiscountAmount)}"
                    } else {
                        pharmStrings.sellAddDiscount
                    },
                    style = PharmText.micro.copy(
                        color = if (cartDiscountAmount > 0.0) t.colors.discount else t.colors.accent,
                    ),
                )
            }
        }

        AnimatedVisibility(visible = open) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                CartTotalsRow(pharmStrings.sellTotal, fmtBaht(grossSubtotal), color = t.colors.fg2)
                if (itemDiscountTotal > 0.0) {
                    CartTotalsRow(pharmStrings.sellDiscountLine, "−${fmtBaht(itemDiscountTotal)}", color = t.colors.discount)
                }
                if (cartDiscountAmount > 0.0) {
                    CartTotalsRow(cartDiscountLabel(cartDiscount), "−${fmtBaht(cartDiscountAmount)}", color = t.colors.discount)
                }
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
private fun CartTotalsRow(label: String, value: String, color: Color) {
    val t = pharmTokens
    Row(
        modifier = Modifier.fillMaxWidth(),
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
