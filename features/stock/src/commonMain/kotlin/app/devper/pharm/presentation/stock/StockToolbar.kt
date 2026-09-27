package app.devper.pharm.presentation.stock

import app.devper.pharm.ui.components.LocalRolePermissions
import app.devper.pharm.presentation.stock.i18n.label

import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import app.devper.pharm.ui.designsystem.PharmButton
import app.devper.pharm.ui.designsystem.PharmButtonSize
import app.devper.pharm.ui.designsystem.PharmAction
import app.devper.pharm.ui.designsystem.PharmToolbarMenu
import app.devper.pharm.ui.designsystem.PharmFilterChip
import app.devper.pharm.ui.designsystem.PharmIcons
import app.devper.pharm.ui.designsystem.PharmListToolbar
import app.devper.pharm.ui.designsystem.PharmSingleSelectChips
import app.devper.pharm.ui.i18n.pharmStrings

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun StockToolbar(
    query: String,
    typeFilter: StockTypeFilter,
    callbacks: StockCallbacks,
    modifier: Modifier = Modifier,
) {
    val s = pharmStrings
    val permissions = LocalRolePermissions.current
    val exportHeaders = listOf(
        s.stockHeaderName,
        s.stockHeaderGeneric,
        s.stockHeaderCategory,
        s.stockHeaderBarcode,
        s.stockHeaderStock,
        s.stockHeaderUnit,
        s.stockHeaderMinStock,
        s.stockHeaderCostPrice,
        s.stockHeaderSellPrice,
        s.stockHeaderRegistration,
    )
    PharmListToolbar(
        modifier = modifier,
        subtitle = pharmStrings.stockSubtitle,
        searchValue = query,
        onSearchChange = callbacks.onQueryChange,
        searchPlaceholder = pharmStrings.stockSearchPlaceholder,
        primaryAction = if (permissions.canEditDrugs) {
            { StockAddDrugButton(callbacks = callbacks) }
        } else null,
        actions = {
            PharmToolbarMenu(
                actions = listOfNotNull(
                    PharmAction(
                        label = "Excel",
                        onClick = { callbacks.onExportExcel(exportHeaders) },
                        icon = PharmIcons.Excel,
                    ),
                    PharmAction(
                        label = s.stockActionImport,
                        onClick = callbacks.onImport,
                        icon = PharmIcons.Imports,
                    ).takeIf { permissions.canEditDrugs },
                ),
                promotedAction = PharmAction(
                    label = s.stockActionPurchase,
                    onClick = callbacks.onOpenReorderSuggestions,
                    icon = PharmIcons.OfflineSync,
                ).takeIf { permissions.canManageStock },
            )
        },
        filters = {
            PharmSingleSelectChips(
                chips = StockTypeFilter.entries.map { PharmFilterChip(id = it.name, label = it.label(pharmStrings)) },
                activeId = typeFilter.name,
                onSelect = { id -> callbacks.onTypeFilterChange(StockTypeFilter.valueOf(id)) },
            )
        },
    )
}

@Composable
private fun StockAddDrugButton(callbacks: StockCallbacks) {
    PharmButton(
        label = pharmStrings.stockAddDrugCta,
        onClick = callbacks.onAddDrug,
        size = PharmButtonSize.Sm,
        leadingIcon = { Icon(PharmIcons.Plus, contentDescription = null) },
    )
}
