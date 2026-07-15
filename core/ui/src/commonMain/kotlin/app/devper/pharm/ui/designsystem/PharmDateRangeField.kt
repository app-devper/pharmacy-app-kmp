package app.devper.pharm.ui.designsystem

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import app.devper.pharm.ui.i18n.pharmStrings
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.Dp
import app.devper.pharm.ui.components.PharmBreakpoint
import app.devper.pharm.ui.theme.PharmText
import app.devper.pharm.ui.theme.pharmTokens
import app.devper.pharm.ui.common.pharmClickable
import app.devper.pharm.ui.theme.tabular

data class PharmDateRange(
    val fromMillis: Long? = null,
    val toMillis: Long? = null,
)

data class PharmDateQuickPeriod(
    val label: String,
    val fromMillis: Long,
    val toMillis: Long,
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PharmDateRangeField(
    range: PharmDateRange,
    onRangeChange: (PharmDateRange) -> Unit,
    formatDate: (Long) -> String,
    modifier: Modifier = Modifier,
    fromLabel: String = pharmStrings.commonFrom,
    toLabel: String = pharmStrings.commonTo,
    quickPeriods: List<PharmDateQuickPeriod> = emptyList(),
) {
    var pickingFrom by remember { mutableStateOf(false) }
    var pickingTo by remember { mutableStateOf(false) }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
            if (shouldStackDateRange(maxWidth)) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    DateField(
                        label = fromLabel,
                        valueMillis = range.fromMillis,
                        formatDate = formatDate,
                        onClick = { pickingFrom = true },
                        modifier = Modifier.fillMaxWidth(),
                    )
                    DateField(
                        label = toLabel,
                        valueMillis = range.toMillis,
                        formatDate = formatDate,
                        onClick = { pickingTo = true },
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
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
                }
            }
        }
        if (quickPeriods.isNotEmpty()) {
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                quickPeriods.forEach { period ->
                    QuickPeriodChip(
                        label = period.label,
                        onClick = {
                            onRangeChange(PharmDateRange(fromMillis = period.fromMillis, toMillis = period.toMillis))
                        },
                    )
                }
            }
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

internal fun shouldStackDateRange(width: Dp): Boolean = width < PharmBreakpoint.Stack

@Composable
private fun DateField(
    label: String,
    valueMillis: Long?,
    formatDate: (Long) -> String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val t = pharmTokens
    val display = valueMillis?.let(formatDate)

    Row(
        modifier = modifier
            .heightIn(min = t.dimens.controlHeight)
            .clip(t.shapes.md)
            .border(1.dp, t.colors.border, t.shapes.md)
            .background(t.colors.surface)
            .pharmClickable(role = Role.Button, shape = t.shapes.md, onClick = onClick)
            .padding(horizontal = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            style = PharmText.micro.copy(color = t.colors.fgMuted),
        )
        Text(
            text = display ?: "—",
            style = PharmText.bodySm.copy(
                color = if (display != null) t.colors.fg1 else t.colors.fgMuted,
            ).tabular(),
            maxLines = 1,
            modifier = Modifier.weight(1f),
        )
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
            .pharmClickable(role = Role.Button, shape = t.shapes.md, onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(text = label, style = PharmText.bodySm)
    }
}

@Composable
private fun DatePickerSheet(
    initialMillis: Long?,
    onPick: (Long?) -> Unit,
) {
    PharmDatePicker(initialMillis = initialMillis, onPick = onPick)
}
