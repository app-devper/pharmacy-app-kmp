package app.devper.pharm.presentation.stock

import app.devper.pharm.common.value.Money
import app.devper.pharm.common.value.Quantity

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.fillMaxSize
import app.devper.pharm.domain.model.Drug
import app.devper.pharm.presentation.stock.i18n.localizeStock
import app.devper.pharm.ui.components.ErrorBottomSheet
import app.devper.pharm.ui.theme.PharmacyTheme
import androidx.compose.ui.tooling.preview.Preview
import app.devper.pharm.ui.designsystem.PharmEmptyState
import app.devper.pharm.ui.designsystem.PharmButton
import app.devper.pharm.ui.designsystem.PharmButtonSize
import app.devper.pharm.ui.designsystem.PharmIcons
import app.devper.pharm.ui.designsystem.PharmListResultLine
import app.devper.pharm.ui.designsystem.PharmListScaffold
import app.devper.pharm.ui.designsystem.PharmListSkeleton
import app.devper.pharm.ui.i18n.pharmStrings

@Composable
fun StockContent(
    state: StockUiState,
    callbacks: StockCallbacks = StockCallbacks(),
) {
    val visible = state.filtered
    val searching = state.query.isNotBlank() || state.typeFilter != StockTypeFilter.All

    PharmListScaffold(
        metrics = { StockMetricsRow(drugs = state.drugs, expiringSoonCount = state.expiringSoonCount, onOpenExpiry = callbacks.onOpenExpiry) },
        toolbar = {
            StockToolbar(
                query = state.query,
                typeFilter = state.typeFilter,
                callbacks = callbacks,
            )
        },
        resultLine = {
            PharmListResultLine(
                total = state.drugs.size,
                noun = pharmStrings.movementsCountNoun,
                visible = visible.size,
                searching = searching,
            )
        },
    ) {
        when {
            state.loading && state.drugs.isEmpty() ->
                PharmListSkeleton(modifier = Modifier.fillMaxSize())
            state.drugs.isEmpty() -> PharmEmptyState(
                icon = PharmIcons.Stock,
                title = pharmStrings.stockListEmpty,
                action = {
                    PharmButton(
                        label = pharmStrings.stockAddDrugCta,
                        onClick = callbacks.onAddDrug,
                        size = PharmButtonSize.Sm,
                    )
                },
            )
            else -> StockTable(
                drugs = visible,
                callbacks = callbacks,
                emptySearching = searching,
            )
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
