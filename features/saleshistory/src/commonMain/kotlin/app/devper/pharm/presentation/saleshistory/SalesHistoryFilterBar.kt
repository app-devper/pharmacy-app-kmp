package app.devper.pharm.presentation.saleshistory

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.devper.pharm.presentation.saleshistory.internal.formatYmdDisplay
import app.devper.pharm.presentation.saleshistory.internal.ymdToMillis
import app.devper.pharm.ui.designsystem.PharmButton
import app.devper.pharm.ui.designsystem.PharmButtonSize
import app.devper.pharm.ui.designsystem.PharmDateRange
import app.devper.pharm.ui.designsystem.PharmDateRangeField
import app.devper.pharm.ui.designsystem.PharmTextField

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun SalesHistoryFilterBar(
    state: SalesHistoryUiState,
    callbacks: SalesHistoryCallbacks,
    modifier: Modifier = Modifier,
) {
    val range = PharmDateRange(
        fromMillis = ymdToMillis(state.from),
        toMillis = ymdToMillis(state.to),
    )

    FlowRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Box(modifier = Modifier.weight(1f)) {
            PharmDateRangeField(
                range = range,
                onRangeChange = { next ->
                    if (next.fromMillis != range.fromMillis) callbacks.onFromMillisChange(next.fromMillis)
                    if (next.toMillis != range.toMillis) callbacks.onToMillisChange(next.toMillis)
                },
                formatDate = { millis -> formatYmdDisplay(millis) },
            )
        }
        Box(modifier = Modifier.weight(1f)) {
            PharmTextField(
                value = state.query,
                onValueChange = callbacks.onQueryChange,
                placeholder = "เลขบิล หรือ ชื่อลูกค้า…",
            )
        }
        PharmButton(
            label = "ค้นหา",
            onClick = callbacks.onApplyFilter,
            size = PharmButtonSize.Md,
        )
    }
}
