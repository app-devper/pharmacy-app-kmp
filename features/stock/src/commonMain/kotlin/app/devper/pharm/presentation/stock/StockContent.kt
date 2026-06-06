package app.devper.pharm.presentation.stock

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import app.devper.pharm.domain.model.Drug
import app.devper.pharm.ui.components.ErrorBottomSheet
import app.devper.pharm.ui.theme.PharmacyTheme
import app.devper.pharm.ui.theme.pharmTokens
import androidx.compose.ui.tooling.preview.Preview
import app.devper.pharm.ui.designsystem.PharmListResultLine
import app.devper.pharm.ui.designsystem.PharmListSkeleton

@Composable
fun StockContent(
    state: StockUiState,
    callbacks: StockCallbacks = StockCallbacks(),
) {
    val t = pharmTokens
    val visible = state.filtered

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(t.colors.bgPage),
    ) {
        StockToolbar(
            query = state.query,
            typeFilter = state.typeFilter,
            callbacks = callbacks,
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            StockMetricsRow(drugs = state.drugs)

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .clip(t.shapes.lg)
                    .background(t.colors.surface)
                    .border(1.dp, t.colors.borderSubtle, t.shapes.lg),
            ) {
                PharmListResultLine(
                    total = state.drugs.size,
                    noun = "รายการ",
                    visible = visible.size,
                    searching = state.query.isNotBlank() || state.typeFilter != StockTypeFilter.All,
                )
                Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(t.colors.divider))

                when {
                    state.loading && state.drugs.isEmpty() ->
                        PharmListSkeleton(modifier = Modifier.fillMaxSize())
                    else -> StockTable(
                        drugs = visible,
                        callbacks = callbacks,
                        emptySearching = state.query.isNotBlank() || state.typeFilter != StockTypeFilter.All,
                    )
                }
            }
        }
    }

    ErrorBottomSheet(message = state.error, onDismiss = callbacks.onDismissError)
}

private val sampleDrugs = listOf(
    Drug(
        id = "1", name = "พาราเซตามอล 500mg", genericName = "Paracetamol 500mg", strength = "500mg",
        type = "cur", regNo = "1A 123/45", costPrice = 1.20, sellPrice = 2.0, stock = 480,
        unit = "เม็ด", barcode = "8851234567001", minStock = 20, reportTypes = emptyList(),
    ),
    Drug(
        id = "2", name = "อะม็อกซีซิลลิน 500mg", genericName = "Amoxicillin 500mg", strength = "500mg",
        type = "cur", regNo = "1A 091/52", costPrice = 5.50, sellPrice = 8.0, stock = 120,
        unit = "แคปซูล", barcode = "8851234567002", minStock = 30, reportTypes = listOf("ky11"),
    ),
    Drug(
        id = "3", name = "ไอบูโพรเฟน 400mg", genericName = "Ibuprofen 400mg", strength = "400mg",
        type = "cur", regNo = "1A 220/61", costPrice = 1.80, sellPrice = 3.0, stock = 0,
        unit = "เม็ด", barcode = "8851234567003", minStock = 20, reportTypes = emptyList(),
    ),
    Drug(
        id = "4", name = "ฟ้าทะลายโจร แคปซูล", genericName = "Andrographis", strength = "400mg",
        type = "herb", regNo = "G 808/63", costPrice = 75.0, sellPrice = 120.0, stock = 38,
        unit = "ขวด", barcode = "8851234567004", minStock = 10, reportTypes = emptyList(),
    ),
    Drug(
        id = "5", name = "ทรามาดอล 50mg", genericName = "Tramadol 50mg", strength = "50mg",
        type = "cur", regNo = "1A 200/58", costPrice = 6.20, sellPrice = 9.0, stock = -4,
        unit = "แคปซูล", barcode = "8851234567011", minStock = 30, reportTypes = listOf("ky10", "ky11"),
    ),
    Drug(
        id = "6", name = "วิตามินซี 1000mg", genericName = "Vit. C 1000mg", strength = "1000mg",
        type = "supp", regNo = "S 105/60", costPrice = 110.0, sellPrice = 180.0, stock = 64,
        unit = "ขวด", barcode = "8851234567005", minStock = 5, reportTypes = emptyList(),
    ),
)

@Preview
@Composable
private fun StockContent_Loaded_Preview() {
    PharmacyTheme {
        StockContent(state = StockUiState(drugs = sampleDrugs))
    }
}

@Preview
@Composable
private fun StockContent_Empty_Preview() {
    PharmacyTheme {
        StockContent(state = StockUiState(drugs = emptyList()))
    }
}

@Preview
@Composable
private fun StockContent_Searching_Preview() {
    PharmacyTheme {
        StockContent(state = StockUiState(drugs = sampleDrugs, query = "วิตามิน"))
    }
}

@Preview
@Composable
private fun StockContent_Loading_Preview() {
    PharmacyTheme {
        StockContent(state = StockUiState(loading = true))
    }
}
