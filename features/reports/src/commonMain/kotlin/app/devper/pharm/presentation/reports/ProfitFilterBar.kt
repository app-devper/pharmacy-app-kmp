package app.devper.pharm.presentation.reports

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.devper.pharm.presentation.reports.internal.ProfitQuickPeriod
import app.devper.pharm.presentation.reports.internal.formatYmdDisplay
import app.devper.pharm.presentation.reports.internal.resolve
import app.devper.pharm.presentation.reports.internal.todayDate
import app.devper.pharm.presentation.reports.internal.ymdToMillis
import app.devper.pharm.ui.designsystem.PharmButton
import app.devper.pharm.ui.designsystem.PharmButtonSize
import app.devper.pharm.ui.designsystem.PharmButtonVariant
import app.devper.pharm.ui.designsystem.PharmDateQuickPeriod
import app.devper.pharm.ui.designsystem.PharmDateRange
import app.devper.pharm.ui.designsystem.PharmDateRangeField
import app.devper.pharm.ui.designsystem.PharmFilterChip
import app.devper.pharm.ui.designsystem.PharmIcons
import app.devper.pharm.ui.designsystem.PharmListToolbar
import app.devper.pharm.ui.designsystem.PharmSingleSelectChips
import app.devper.pharm.ui.theme.PharmText
import app.devper.pharm.ui.theme.pharmTokens

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun ProfitFilterBar(
    state: ProfitUiState,
    callbacks: ProfitCallbacks,
    modifier: Modifier = Modifier,
) {
    val t = pharmTokens
    val range = PharmDateRange(
        fromMillis = state.dateRange.fromMillis,
        toMillis = state.dateRange.toMillis,
    )
    val quickPeriods = remember(todayDate(state.dateRange.tz)) {
        ProfitQuickPeriod.entries.map { period ->
            val resolved = period.resolve(state.dateRange.tz)
            PharmDateQuickPeriod(
                label = period.label,
                fromMillis = resolved.fromMillis,
                toMillis = resolved.toMillis,
            )
        }
    }
    val sortChips = remember {
        ProfitSort.entries.map { PharmFilterChip(id = it.name, label = it.label) }
    }

    PharmListToolbar(
        title = "กำไรต่อยา",
        subtitle = "กำไรแยกตามรายการยาในช่วงที่เลือก",
        modifier = modifier,
        actions = {
            PharmButton(
                label = "Excel",
                onClick = callbacks.onExportExcel,
                size = PharmButtonSize.Md,
                variant = PharmButtonVariant.Outline,
                leadingIcon = {
                    Icon(
                        imageVector = PharmIcons.Excel,
                        contentDescription = null,
                        tint = t.colors.successFg,
                        modifier = Modifier.size(16.dp),
                    )
                },
            )
        },
        filters = {
            Box(modifier = Modifier.weight(1f)) {
                PharmDateRangeField(
                    range = range,
                    onRangeChange = { next ->
                        if (next.fromMillis != range.fromMillis) callbacks.onFromMillisChange(next.fromMillis)
                        if (next.toMillis != range.toMillis) callbacks.onToMillisChange(next.toMillis)
                    },
                    formatDate = { millis -> formatYmdDisplay(millis, state.dateRange.tz) },
                    quickPeriods = quickPeriods,
                )
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "เรียงตาม",
                    style = PharmText.bodySm.copy(color = t.colors.fg3),
                )
                PharmSingleSelectChips(
                    chips = sortChips,
                    activeId = state.sort.name,
                    onSelect = { id -> callbacks.onSortChange(ProfitSort.valueOf(id)) },
                )
            }
        },
    )
}
