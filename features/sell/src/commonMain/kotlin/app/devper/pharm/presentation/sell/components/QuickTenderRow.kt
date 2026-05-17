package app.devper.pharm.presentation.sell.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.devper.pharm.ui.theme.PharmacyTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
internal fun QuickTenderRow(
    total: Double,
    enabled: Boolean,
    onPick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        TenderChip(label = "฿100", value = "100", enabled = enabled, onPick = onPick)
        TenderChip(label = "฿500", value = "500", enabled = enabled, onPick = onPick)
        TenderChip(label = "฿1,000", value = "1000", enabled = enabled, onPick = onPick)

        TenderChip(
            label = "ตามยอด · ฿${bahtAmount(total)}",
            value = bahtAmount(total),
            enabled = enabled && total > 0,
            onPick = onPick,
            primary = true,
        )
    }
}

@Composable
private fun TenderChip(
    label: String,
    value: String,
    enabled: Boolean,
    onPick: (String) -> Unit,
    primary: Boolean = false,
) {
    AssistChip(
        onClick = { onPick(value) },
        enabled = enabled,
        label = {
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
            )
        },
        colors = if (primary) {
            AssistChipDefaults.assistChipColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                labelColor = MaterialTheme.colorScheme.onPrimaryContainer,
            )
        } else {
            AssistChipDefaults.assistChipColors()
        },
    )
}

@Preview
@Composable
private fun QuickTenderRow_Preview() {
    PharmacyTheme {
        QuickTenderRow(total = 245.50, enabled = true, onPick = {})
    }
}
