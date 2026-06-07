package app.devper.pharm.presentation.saleshistory

import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import app.devper.pharm.presentation.saleshistory.internal.formatYmdDisplay
import app.devper.pharm.presentation.saleshistory.internal.ymdToMillis
import app.devper.pharm.ui.designsystem.PharmButton
import app.devper.pharm.ui.designsystem.PharmButtonSize
import app.devper.pharm.ui.designsystem.PharmDateRange
import app.devper.pharm.ui.designsystem.PharmDateRangeField
import app.devper.pharm.ui.designsystem.PharmListToolbar
import app.devper.pharm.ui.i18n.pharmStrings

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
        title = s.navSalesHistory,
        subtitle = s.salesHistorySubtitle,
        modifier = modifier,
        searchValue = state.query,
        onSearchChange = callbacks.onQueryChange,
        searchPlaceholder = s.salesHistorySearchPlaceholder,
        filters = {
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
