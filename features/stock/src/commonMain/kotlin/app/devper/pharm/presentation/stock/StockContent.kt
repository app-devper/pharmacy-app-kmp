package app.devper.pharm.presentation.stock

import app.devper.pharm.common.value.Money
import app.devper.pharm.common.value.Quantity

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import app.devper.pharm.presentation.stock.i18n.localizeStock
import app.devper.pharm.ui.components.ErrorBottomSheet
import app.devper.pharm.ui.theme.PharmacyTheme
import app.devper.pharm.ui.theme.pharmTokens
import androidx.compose.ui.tooling.preview.Preview
import app.devper.pharm.ui.designsystem.PharmListResultLine
import app.devper.pharm.ui.designsystem.PharmListSkeleton
import app.devper.pharm.ui.i18n.pharmStrings

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
        StockMetricsRow(
            drugs = state.drugs,
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp),
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .clip(t.shapes.lg)
                    .background(t.colors.surface)
                    .border(1.dp, t.colors.borderSubtle, t.shapes.lg),
            ) {
                StockToolbar(
                    query = state.query,
                    typeFilter = state.typeFilter,
                    callbacks = callbacks,
                )
                Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(t.colors.divider))
                PharmListResultLine(
                    total = state.drugs.size,
                    noun = pharmStrings.movementsCountNoun,
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

    ErrorBottomSheet(message = state.errorState?.localizeStock(pharmStrings), onDismiss = callbacks.onDismissError)
}

private val sampleDrugs = listOf(
    Drug(
        id = "1", name = "พาราเซตามอล 500mg", genericName = "Paracetamol 500mg", strength = "500mg",
        type = "cur", regNo = "1A 123/45", costPrice = Money(1.20), sellPrice = Money(2.0), stock = Quantity(480),
        unit = "เม็ด", barcode = "8851234567001", minStock = Quantity(20), reportTypes = emptyList(),
    ),
    Drug(
        id = "2", name = "อะม็อกซีซิลลิน 500mg", genericName = "Amoxicillin 500mg", strength = "500mg",
        type = "cur", regNo = "1A 091/52", costPrice = Money(5.50), sellPrice = Money(8.0), stock = Quantity(120),
        unit = "แคปซูล", barcode = "8851234567002", minStock = Quantity(30), reportTypes = listOf("ky11"),
    ),
    Drug(
        id = "3", name = "ไอบูโพรเฟน 400mg", genericName = "Ibuprofen 400mg", strength = "400mg",
        type = "cur", regNo = "1A 220/61", costPrice = Money(1.80), sellPrice = Money(3.0), stock = Quantity(0),
        unit = "เม็ด", barcode = "8851234567003", minStock = Quantity(20), reportTypes = emptyList(),
    ),
    Drug(
        id = "4", name = "ฟ้าทะลายโจร แคปซูล", genericName = "Andrographis", strength = "400mg",
        type = "herb", regNo = "G 808/63", costPrice = Money(75.0), sellPrice = Money(120.0), stock = Quantity(38),
        unit = "ขวด", barcode = "8851234567004", minStock = Quantity(10), reportTypes = emptyList(),
    ),
    Drug(
        id = "5", name = "ทรามาดอล 50mg", genericName = "Tramadol 50mg", strength = "50mg",
        type = "cur", regNo = "1A 200/58", costPrice = Money(6.20), sellPrice = Money(9.0), stock = Quantity(-4),
        unit = "แคปซูล", barcode = "8851234567011", minStock = Quantity(30), reportTypes = listOf("ky10", "ky11"),
    ),
    Drug(
        id = "6", name = "วิตามินซี 1000mg", genericName = "Vit. C 1000mg", strength = "1000mg",
        type = "supp", regNo = "S 105/60", costPrice = Money(110.0), sellPrice = Money(180.0), stock = Quantity(64),
        unit = "ขวด", barcode = "8851234567005", minStock = Quantity(5), reportTypes = emptyList(),
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
