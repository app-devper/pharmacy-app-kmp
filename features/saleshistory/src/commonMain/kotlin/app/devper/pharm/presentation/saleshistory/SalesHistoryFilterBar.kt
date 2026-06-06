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

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun SalesHistoryListToolbar(
    state: SalesHistoryUiState,
    callbacks: SalesHistoryCallbacks,
    modifier: Modifier = Modifier,
) {
    val range = PharmDateRange(
        fromMillis = ymdToMillis(state.from),
        toMillis = ymdToMillis(state.to),
    )

    PharmListToolbar(
        title = "ประวัติการขาย",
        subtitle = "บิลขายย้อนหลังและการคืน/ยกเลิก",
        modifier = modifier,
        searchValue = state.query,
        onSearchChange = callbacks.onQueryChange,
        searchPlaceholder = "เลขบิล หรือ ชื่อลูกค้า…",
        filters = {
            PharmDateRangeField(
                range = range,
                onRangeChange = { next ->
                    if (next.fromMillis != range.fromMillis) callbacks.onFromMillisChange(next.fromMillis)
                    if (next.toMillis != range.toMillis) callbacks.onToMillisChange(next.toMillis)
                },
                formatDate = { millis -> formatYmdDisplay(millis) },
                modifier = Modifier.weight(1f),
            )
        },
        actions = {
            PharmButton(
                label = "ค้นหา",
                onClick = callbacks.onApplyFilter,
                size = PharmButtonSize.Sm,
            )
        },
    )
}
