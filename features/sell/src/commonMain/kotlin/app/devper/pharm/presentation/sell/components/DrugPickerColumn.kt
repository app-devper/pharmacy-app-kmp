package app.devper.pharm.presentation.sell.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import app.devper.pharm.domain.model.Drug
import app.devper.pharm.domain.pricing.resolvePrice
import app.devper.pharm.ui.designsystem.DrugCard
import app.devper.pharm.ui.designsystem.DrugCardType
import app.devper.pharm.ui.designsystem.PharmEmptyState
import app.devper.pharm.ui.designsystem.PharmIcons
import app.devper.pharm.ui.designsystem.PharmTextField
import app.devper.pharm.ui.theme.PharmText
import app.devper.pharm.ui.theme.pharmTokens
import app.devper.pharm.ui.designsystem.PharmCircularProgress

private val DRUG_CARD_HEIGHT = 148.dp

@Composable
fun DrugPickerColumn(
    query: String,
    onQueryChange: (String) -> Unit,
    drugs: List<Drug>,
    visible: List<Drug>,
    loading: Boolean,
    activeTier: String,
    onAdd: (Drug) -> Unit,
    modifier: Modifier = Modifier,
) {
    val t = pharmTokens
    val searchFocus = remember { FocusRequester() }
    LaunchedEffect(Unit) { runCatching { searchFocus.requestFocus() } }
    val onSubmitSearch = {
        if (query.isNotBlank()) {
            visible.firstOrNull()?.let { drug ->
                onAdd(drug)
                onQueryChange("")
            }
        }
    }
    Column(modifier = modifier.fillMaxSize().background(t.colors.bgPage)) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(t.colors.surface)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            SearchBar(
                query = query,
                onChange = onQueryChange,
                onSubmit = onSubmitSearch,
                focusRequester = searchFocus,
                modifier = Modifier.weight(1f),
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(t.colors.divider),
        )

        ResultLine(query, total = drugs.size, visibleCount = visible.size)

        when {
            loading && drugs.isEmpty() -> Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) { PharmCircularProgress(color = t.colors.accent) }

            visible.isEmpty() && !loading -> EmptyState(searching = query.isNotBlank())

            else -> BoxWithConstraints(modifier = Modifier.fillMaxSize()) {

                val columns = when {
                    maxWidth >= 1280.dp -> 4
                    maxWidth >= 640.dp  -> 3
                    else                -> 2
                }
                LazyVerticalGrid(
                    columns = GridCells.Fixed(columns),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    items(visible, key = { it.id }) { drug ->
                        DrugCard(
                            modifier = Modifier.height(DRUG_CARD_HEIGHT),
                            name = drug.name,
                            generic = drug.genericName,
                            price = resolvePrice(drug.sellPrice, drug.prices, activeTier),
                            stock = drug.stock,
                            unit = drug.unit ?: "หน่วย",
                            type = inferType(drug),
                            altUnitCount = drug.altUnits.count { !it.hidden },
                            kyForm = inferKyForm(drug),
                            lowStockThreshold = drug.minStock.coerceAtLeast(20),
                            onClick = { onAdd(drug) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SearchBar(
    query: String,
    onChange: (String) -> Unit,
    onSubmit: () -> Unit,
    focusRequester: FocusRequester,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        PharmTextField(
            value = query,
            onValueChange = onChange,
            placeholder = "ค้นหาด้วยชื่อการค้า ชื่อสามัญ หรือบาร์โค้ด (F2)",
            modifier = Modifier.weight(1f),
            imeAction = ImeAction.Search,
            onImeAction = onSubmit,
            focusRequester = focusRequester,
            leadingSlot = null,
            trailingSlot = null,
        )
        ScannerActivePill()
    }
}

@Composable
private fun ScannerActivePill() {
    val t = pharmTokens
    Row(
        modifier = Modifier
            .clip(t.shapes.md)
            .background(t.colors.accentBgSoft)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .clip(t.shapes.pill)
                .background(t.colors.successFg),
        )
        Text(
            text = "สแกนเนอร์เปิดอยู่",
            style = PharmText.badge.copy(color = t.colors.accent),
        )
    }
}

@Composable
private fun ResultLine(query: String, total: Int, visibleCount: Int) {
    val text = if (query.isBlank()) "ทั้งหมด $total รายการ" else "พบ $visibleCount จาก $total"
    Text(
        text = text,
        style = PharmText.micro,
        modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
    )
}

@Composable
private fun EmptyState(searching: Boolean) {
    PharmEmptyState(
        icon = if (searching) PharmIcons.Search else PharmIcons.Imports,
        title = if (searching) "ไม่พบยาที่ค้นหา" else "ยังไม่มีรายการยาในคลัง",
    )
}

private fun inferType(drug: Drug): DrugCardType {
    val t = drug.type?.trim()?.lowercase().orEmpty()
    return when {
        t.contains("herb")    || t.contains("สมุนไพร")  -> DrugCardType.Herb
        t.contains("supp")    || t.contains("อาหารเสริม") -> DrugCardType.Supplement
        else                                              -> DrugCardType.Rx
    }
}

private fun inferKyForm(drug: Drug): Int? {

    val rts = drug.reportTypes.map { it.lowercase() }
    return when {
        "ky12" in rts -> 12
        "ky11" in rts -> 11
        "ky10" in rts -> 10
        else -> null
    }
}
