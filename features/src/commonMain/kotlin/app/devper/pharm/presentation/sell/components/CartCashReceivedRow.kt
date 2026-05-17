package app.devper.pharm.presentation.sell.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import app.devper.pharm.ui.designsystem.PharmTextField
import app.devper.pharm.ui.theme.PharmText
import app.devper.pharm.ui.theme.PharmacyTheme
import app.devper.pharm.ui.theme.fmtBaht
import app.devper.pharm.ui.theme.pharmTokens
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun CartCashReceivedRow(
    received: String,
    total: Double,
    change: Double,
    checkingOut: Boolean,
    onReceivedChange: (String) -> Unit,
) {
    val t = pharmTokens
    val receivedNum = received.toDoubleOrNull() ?: 0.0
    val short = total - receivedNum
    val isShort = receivedNum > 0.0 && short > 0.0

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                "รับเงิน",
                style = PharmText.bodySm.copy(color = t.colors.fg2),
                modifier = Modifier.weight(1f),
            )
            Box(modifier = Modifier.width(140.dp)) {
                PharmTextField(
                    value = received,
                    onValueChange = { v -> onReceivedChange(v.filter { c -> c.isDigit() || c == '.' }) },
                    placeholder = "0",
                    keyboardType = KeyboardType.Decimal,
                    enabled = !checkingOut,
                    isWarning = isShort,
                )
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                "ทอน",
                style = PharmText.bodySm.copy(color = t.colors.fg3),
                modifier = Modifier.weight(1f),
            )
            val shortfall = short.coerceAtLeast(0.0)
            val changeColor = if (isShort) t.colors.dangerFg else t.colors.successFg
            Text(
                text = if (isShort) "ขาด ${fmtBaht(shortfall)}" else fmtBaht(change),
                style = PharmText.bodySm.copy(
                    color = changeColor,
                    fontWeight = FontWeight.SemiBold,
                ),
            )
        }
    }
}

@Preview
@Composable
private fun CartCashReceivedRow_Sufficient_Preview() {
    PharmacyTheme {
        CartCashReceivedRow(
            received = "500",
            total = 446.5,
            change = 53.5,
            checkingOut = false,
            onReceivedChange = {},
        )
    }
}

@Preview
@Composable
private fun CartCashReceivedRow_Short_Preview() {
    PharmacyTheme {
        CartCashReceivedRow(
            received = "300",
            total = 446.5,
            change = 0.0,
            checkingOut = false,
            onReceivedChange = {},
        )
    }
}
