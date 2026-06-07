package app.devper.pharm.presentation.movements

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.devper.pharm.presentation.movements.internal.formatYmdDisplay
import app.devper.pharm.presentation.movements.internal.ymdToMillis
import app.devper.pharm.ui.designsystem.PharmButton
import app.devper.pharm.ui.designsystem.PharmButtonSize
import app.devper.pharm.ui.designsystem.PharmButtonVariant
import app.devper.pharm.ui.designsystem.PharmDateRange
import app.devper.pharm.ui.designsystem.PharmDateRangeField
import app.devper.pharm.ui.designsystem.PharmIcons
import app.devper.pharm.ui.designsystem.PharmListToolbar
import app.devper.pharm.ui.i18n.pharmStrings

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun MovementsListToolbar(
    state: MovementsUiState,
    callbacks: MovementsCallbacks,
    modifier: Modifier = Modifier,
) {
    val s = pharmStrings
    val range = PharmDateRange(
        fromMillis = state.dateRange.fromMillis,
        toMillis = state.dateRange.toMillis,
    )

    PharmListToolbar(
        title = s.navMovements,
        subtitle = s.movementsSubtitle,
        modifier = modifier,
        searchValue = state.drugName,
        onSearchChange = callbacks.onSearchChange,
        searchPlaceholder = s.movementsSearchPlaceholder,
        filters = {
            MovementsTypeChips(
                activeIds = state.activeTypeIds,
                onToggle = callbacks.onToggleType,
                modifier = Modifier.fillMaxWidth(),
            )
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
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                PharmButton(
                    label = s.commonSearch,
                    onClick = callbacks.onApplyFilter,
                    size = PharmButtonSize.Sm,
                )
                PharmButton(
                    label = "Excel",
                    onClick = callbacks.onExportExcel,
                    variant = PharmButtonVariant.Outline,
                    size = PharmButtonSize.Sm,
                    leadingIcon = {
                        Icon(
                            imageVector = PharmIcons.Excel,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                        )
                    },
                )
            }
        },
    )
}
