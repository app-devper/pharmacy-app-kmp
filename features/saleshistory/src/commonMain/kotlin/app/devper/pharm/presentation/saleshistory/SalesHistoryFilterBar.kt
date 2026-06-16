package app.devper.pharm.presentation.saleshistory

import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import app.devper.pharm.presentation.saleshistory.internal.formatYmdDisplay
import app.devper.pharm.ui.designsystem.PharmButton
import app.devper.pharm.ui.designsystem.PharmButtonSize
import app.devper.pharm.ui.designsystem.PharmDateRange
import app.devper.pharm.ui.designsystem.PharmDateRangeField
import app.devper.pharm.ui.designsystem.PharmFilterChip
import app.devper.pharm.ui.designsystem.PharmListToolbar
import app.devper.pharm.ui.designsystem.PharmSingleSelectChips
import app.devper.pharm.ui.i18n.pharmStrings
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.minus
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun SalesHistoryListToolbar(
    state: SalesHistoryUiState,
    callbacks: SalesHistoryCallbacks,
    modifier: Modifier = Modifier,
) {
    val s = pharmStrings
    val range = PharmDateRange(
        fromMillis = state.dateRange.fromMillis,
        toMillis = state.dateRange.toMillis,
    )

    PharmListToolbar(
        modifier = modifier,
        searchValue = state.query,
        onSearchChange = callbacks.onQueryChange,
        searchPlaceholder = s.salesHistorySearchPlaceholder,
        filters = {
            SalesHistoryRangeChips(state = state, onSelectRange = callbacks.onSelectRange)
            PharmDateRangeField(
                range = range,
                onRangeChange = { next ->
                    if (next.fromMillis != range.fromMillis) callbacks.onFromMillisChange(next.fromMillis)
                    if (next.toMillis != range.toMillis) callbacks.onToMillisChange(next.toMillis)
                },
                formatDate = { millis -> formatYmdDisplay(millis, state.dateRange.tz) },
                modifier = Modifier.weight(1f),
            )
        },
        actions = {
            PharmButton(
                label = s.commonSearch,
                onClick = callbacks.onApplyFilter,
                size = PharmButtonSize.Sm,
            )
        },
    )
}

@Composable
private fun SalesHistoryRangeChips(
    state: SalesHistoryUiState,
    onSelectRange: (String, String) -> Unit,
) {
    val s = pharmStrings
    val tz = state.dateRange.tz
    val presets = remember(tz) {
        val today = Clock.System.now().toLocalDateTime(tz).date
        val monthStart = LocalDate(today.year, today.month, 1)
        linkedMapOf(
            "today" to (today.toString() to today.toString()),
            "7d" to (today.minus(DatePeriod(days = 6)).toString() to today.toString()),
            "month" to (monthStart.toString() to today.toString()),
        )
    }
    val current = state.dateRange.from to state.dateRange.to
    val activeId = presets.entries.firstOrNull { it.value == current }?.key

    PharmSingleSelectChips(
        chips = listOf(
            PharmFilterChip(id = "today", label = s.salesHistoryRangeToday),
            PharmFilterChip(id = "7d", label = s.salesHistoryRange7d),
            PharmFilterChip(id = "month", label = s.salesHistoryRangeMonth),
        ),
        activeId = activeId,
        onSelect = { id -> presets[id]?.let { (from, to) -> onSelectRange(from, to) } },
        scrollable = false,
    )
}
