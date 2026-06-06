package app.devper.pharm.ui.designsystem

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import app.devper.pharm.ui.theme.PharmText
import app.devper.pharm.ui.theme.pharmTokens

data class PharmDateRange(
    val fromMillis: Long? = null,
    val toMillis: Long? = null,
)

data class PharmDateQuickPeriod(
    val label: String,
    val fromMillis: Long,
    val toMillis: Long,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PharmDateRangeField(
    range: PharmDateRange,
    onRangeChange: (PharmDateRange) -> Unit,
    formatDate: (Long) -> String,
    modifier: Modifier = Modifier,
    fromLabel: String = "จาก",
    toLabel: String = "ถึง",
    quickPeriods: List<PharmDateQuickPeriod> = emptyList(),
) {
    var pickingFrom by remember { mutableStateOf(false) }
    var pickingTo by remember { mutableStateOf(false) }

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        DateField(
            label = fromLabel,
            valueMillis = range.fromMillis,
            formatDate = formatDate,
            onClick = { pickingFrom = true },
            modifier = Modifier.weight(1f),
        )
        DateField(
            label = toLabel,
            valueMillis = range.toMillis,
            formatDate = formatDate,
            onClick = { pickingTo = true },
            modifier = Modifier.weight(1f),
        )
        quickPeriods.forEach { period ->
            QuickPeriodChip(
                label = period.label,
                onClick = {
                    onRangeChange(PharmDateRange(fromMillis = period.fromMillis, toMillis = period.toMillis))
                },
            )
        }
    }

    if (pickingFrom) {
        DatePickerSheet(
            initialMillis = range.fromMillis,
            onPick = { millis ->
                pickingFrom = false
                if (millis != null) onRangeChange(range.copy(fromMillis = millis))
            },
        )
    }
    if (pickingTo) {
        DatePickerSheet(
            initialMillis = range.toMillis,
            onPick = { millis ->
                pickingTo = false
                if (millis != null) onRangeChange(range.copy(toMillis = millis))
            },
        )
    }
}

@Composable
private fun DateField(
    label: String,
    valueMillis: Long?,
    formatDate: (Long) -> String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val t = pharmTokens
    val display = valueMillis?.let(formatDate) ?: "—"

    Box(
        modifier = modifier
            .clip(t.shapes.md)
            .border(1.dp, t.colors.border, t.shapes.md)
            .background(t.colors.surface)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 10.dp),
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = label,
                style = PharmText.micro,
                modifier = Modifier.align(Alignment.TopStart),
            )
            Text(
                text = display,
                style = PharmText.body,
                modifier = Modifier.align(Alignment.BottomStart).padding(top = 14.dp),
            )
        }
    }
}

@Composable
private fun QuickPeriodChip(label: String, onClick: () -> Unit) {
    val t = pharmTokens
    Box(
        modifier = Modifier
            .heightIn(min = t.dimens.controlHeight)
            .clip(t.shapes.md)
            .border(1.dp, t.colors.border, t.shapes.md)
            .background(t.colors.bgPage)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(text = label, style = PharmText.bodySm)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DatePickerSheet(
    initialMillis: Long?,
    onPick: (Long?) -> Unit,
) {
    val state = key(initialMillis) {
        rememberDatePickerState(initialSelectedDateMillis = initialMillis)
    }
    DatePickerDialog(
        onDismissRequest = { onPick(null) },
        confirmButton = {
            PharmButton(
                label = "ยืนยัน",
                onClick = { onPick(state.selectedDateMillis) },
                size = PharmButtonSize.Sm,
            )
        },
        dismissButton = {
            PharmButton(
                label = "ยกเลิก",
                onClick = { onPick(null) },
                size = PharmButtonSize.Sm,
                variant = PharmButtonVariant.Ghost,
            )
        },
    ) {
        DatePicker(state = state)
    }
}
