package app.devper.pharm.presentation.stock

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
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
import app.devper.pharm.ui.designsystem.PharmSingleSelectChips
import app.devper.pharm.ui.designsystem.PharmTextField

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun StockToolbar(
    query: String,
    typeFilter: StockTypeFilter,
    callbacks: StockCallbacks,
    modifier: Modifier = Modifier,
) {
    FlowRow(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Box(modifier = Modifier.weight(1f).widthIn(max = 280.dp)) {
            PharmTextField(
                value = query,
                onValueChange = callbacks.onQueryChange,
                placeholder = "ค้นหายา ชื่อสามัญ บาร์โค้ด…",
            )
        }
        PharmSingleSelectChips(
            chips = StockTypeFilter.entries.map { PharmFilterChip(id = it.name, label = it.label) },
            activeId = typeFilter.name,
            onSelect = { id -> callbacks.onTypeFilterChange(StockTypeFilter.valueOf(id)) },
            scrollable = false,
        )
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
                label = "นำเข้า",
                onClick = callbacks.onImport,
                variant = PharmButtonVariant.Outline,
                size = PharmButtonSize.Sm,
                leadingIcon = { Icon(PharmIcons.Imports, contentDescription = null) },
            )
            PharmButton(
                label = "สั่งซื้อ",
                onClick = callbacks.onOpenReorderSuggestions,
                variant = PharmButtonVariant.Outline,
                size = PharmButtonSize.Sm,
                leadingIcon = { Icon(PharmIcons.OfflineSync, contentDescription = null) },
            )
            PharmButton(
                label = "เพิ่มยา",
                onClick = callbacks.onAddDrug,
                size = PharmButtonSize.Sm,
                leadingIcon = { Icon(PharmIcons.Plus, contentDescription = null) },
            )
        }
    }
}
