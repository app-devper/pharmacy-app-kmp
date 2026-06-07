package app.devper.pharm.presentation.stock

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.devper.pharm.ui.designsystem.PharmButton
import app.devper.pharm.ui.designsystem.PharmButtonSize
import app.devper.pharm.ui.designsystem.PharmButtonVariant
import app.devper.pharm.ui.designsystem.PharmFilterChip
import app.devper.pharm.ui.designsystem.PharmIcons
import app.devper.pharm.ui.designsystem.PharmListToolbar
import app.devper.pharm.ui.designsystem.PharmSingleSelectChips
import app.devper.pharm.ui.i18n.pharmStrings

@Composable
internal fun StockToolbar(
    query: String,
    typeFilter: StockTypeFilter,
    callbacks: StockCallbacks,
    modifier: Modifier = Modifier,
) {
    PharmListToolbar(
        title = pharmStrings.navStock,
        subtitle = pharmStrings.stockSubtitle,
        modifier = modifier,
        searchValue = query,
        onSearchChange = callbacks.onQueryChange,
        searchPlaceholder = pharmStrings.stockSearchPlaceholder,
        filters = {
            PharmSingleSelectChips(
                chips = StockTypeFilter.entries.map { PharmFilterChip(id = it.name, label = it.label) },
                activeId = typeFilter.name,
                onSelect = { id -> callbacks.onTypeFilterChange(StockTypeFilter.valueOf(id)) },
                modifier = Modifier.fillMaxWidth(),
            )
        },
        actions = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                PharmButton(
                    label = "Excel",
                    onClick = callbacks.onExportExcel,
                    variant = PharmButtonVariant.Outline,
                    size = PharmButtonSize.Sm,
                    leadingIcon = { Icon(PharmIcons.Excel, contentDescription = null) },
                )
                PharmButton(
                    label = pharmStrings.stockActionImport,
                    onClick = callbacks.onImport,
                    variant = PharmButtonVariant.Outline,
                    size = PharmButtonSize.Sm,
                    leadingIcon = { Icon(PharmIcons.Imports, contentDescription = null) },
                )
                PharmButton(
                    label = pharmStrings.stockActionPurchase,
                    onClick = callbacks.onOpenReorderSuggestions,
                    variant = PharmButtonVariant.Outline,
                    size = PharmButtonSize.Sm,
                    leadingIcon = { Icon(PharmIcons.OfflineSync, contentDescription = null) },
                )
                PharmButton(
                    label = pharmStrings.stockAddDrugCta,
                    onClick = callbacks.onAddDrug,
                    size = PharmButtonSize.Sm,
                    leadingIcon = { Icon(PharmIcons.Plus, contentDescription = null) },
                )
            }
        },
    )
}
