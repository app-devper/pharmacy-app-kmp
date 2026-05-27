package app.devper.pharm.presentation.sell.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import org.jetbrains.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.devper.pharm.domain.model.CartDiscount
import app.devper.pharm.ui.common.ShortcutHint
import app.devper.pharm.ui.theme.PharmText
import app.devper.pharm.ui.theme.PharmacyTheme
import app.devper.pharm.ui.theme.fmtBaht
import app.devper.pharm.ui.theme.pharmTokens

@Composable
fun CartTotalsBlock(
    grossSubtotal: Double,
    itemDiscountTotal: Double,
    cartDiscount: CartDiscount,
    cartDiscountAmount: Double,
    total: Double,
    onOpenCartDiscount: () -> Unit,
) {
    val t = pharmTokens
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        CartTotalsRow("ยอดรวม", fmtBaht(grossSubtotal), color = t.colors.fg2)
        if (itemDiscountTotal > 0) {
            CartTotalsRow(
                "ส่วนลดรายการ",
                "−${fmtBaht(itemDiscountTotal)}",
                color = t.colors.discount,
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onOpenCartDiscount)
                .padding(vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            val label = when (cartDiscount) {
                is CartDiscount.None -> "ส่วนลดบิล"
                is CartDiscount.Flat -> "ส่วนลดบิล"
                is CartDiscount.Percent -> "ส่วนลดบิล ${cartDiscount.percent.toInt()}%"
            }
            Text(label, style = PharmText.bodySm.copy(color = t.colors.fg2), modifier = Modifier.weight(1f))
            if (cartDiscountAmount > 0) {
                Text(
                    "−${fmtBaht(cartDiscountAmount)}",
                    style = PharmText.bodySm.copy(color = t.colors.discount),
                )
            } else {
                ShortcutHint(label = "F4", modifier = Modifier.padding(end = 6.dp))
                Text("เพิ่ม ›", style = PharmText.micro.copy(color = t.colors.accent))
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 6.dp),
            verticalAlignment = Alignment.Bottom,
        ) {
            Text("ยอดสุทธิ", style = PharmText.bodySm.copy(color = t.colors.fg3), modifier = Modifier.weight(1f))
            Text(fmtBaht(total), style = PharmText.total)
        }
    }
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
