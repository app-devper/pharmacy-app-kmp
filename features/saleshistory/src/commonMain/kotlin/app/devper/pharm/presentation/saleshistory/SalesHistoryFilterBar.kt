package app.devper.pharm.presentation.saleshistory

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.devper.pharm.ui.format.QuickPeriod
import app.devper.pharm.ui.format.label
import app.devper.pharm.ui.format.millisToBuddhistDisplay
import app.devper.pharm.ui.designsystem.PharmDateRangeField
import app.devper.pharm.ui.designsystem.PharmFilterChip
import app.devper.pharm.ui.designsystem.PharmListToolbar
import app.devper.pharm.ui.designsystem.PharmSingleSelectChips
import app.devper.pharm.ui.i18n.pharmStrings

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun SalesHistoryListToolbar(
    state: SalesHistoryUiState,
    callbacks: SalesHistoryCallbacks,
    modifier: Modifier = Modifier,
) {
    val s = pharmStrings
    PharmListToolbar(
        modifier = modifier,
        subtitle = s.salesHistorySubtitle,
        searchValue = state.query,
        onSearchChange = callbacks.onQueryChange,
        onSearch = callbacks.onApplyFilter,
        searching = state.loading,
        searchPlaceholder = s.salesHistorySearchPlaceholder,
        compactControlsSharedRow = false,
        filters = {
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                itemVerticalAlignment = Alignment.CenterVertically,
            ) {
                PharmDateRangeField(
                    range = state.dateRange.range,
                    onRangeChange = callbacks.onRangeChange,
                    formatDate = { millis -> millisToBuddhistDisplay(millis, state.dateRange.tz) },
                    modifier = Modifier.widthIn(min = 220.dp),
                )
                SalesHistoryRangeChips(state = state, onSelectPeriod = callbacks.onSelectPeriod)
            }
        },
    )
}

private val SALES_HISTORY_PERIODS = listOf(QuickPeriod.Today, QuickPeriod.Last7Days, QuickPeriod.ThisMonth)

@Composable
private fun SalesHistoryRangeChips(
    state: SalesHistoryUiState,
    onSelectPeriod: (QuickPeriod) -> Unit,
) {
    val s = pharmStrings
    PharmSingleSelectChips(
        chips = SALES_HISTORY_PERIODS.map { PharmFilterChip(id = it.name, label = it.label(s)) },
        activeId = state.dateRange.activePeriod(SALES_HISTORY_PERIODS)?.name,
        onSelect = { id -> SALES_HISTORY_PERIODS.firstOrNull { it.name == id }?.let(onSelectPeriod) },
        scrollable = false,
    )
}
