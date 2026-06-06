package app.devper.pharm.presentation.stockcount

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import app.devper.pharm.domain.model.Drug
import app.devper.pharm.presentation.stockcount.components.SubmitConfirmModal
import app.devper.pharm.ui.components.ErrorBottomSheet
import app.devper.pharm.ui.theme.PharmacyTheme
import app.devper.pharm.ui.theme.pharmTokens
import androidx.compose.ui.tooling.preview.Preview
import app.devper.pharm.ui.designsystem.PharmCircularProgress

@Composable
fun StockCountFormContent(
    state: StockCountFormUiState,
    callbacks: StockCountFormCallbacks = StockCountFormCallbacks(),
    history: List<StockCountHistoryEntry> = emptyList(),
) {
    val t = pharmTokens

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(t.colors.bgPage),
    ) {
        StockCountFormToolbar(state = state, callbacks = callbacks)
        Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(t.colors.divider))

        when {
            state.loading && state.drugs.isEmpty() ->
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    PharmCircularProgress(color = t.colors.accent)
                }
            else -> DualPaneBody(state = state, callbacks = callbacks, history = history)
        }
    }

    ErrorBottomSheet(message = state.error, onDismiss = callbacks.onDismissError)

    SubmitConfirmModal(
        open = state.showSubmitConfirm,
        changedCount = state.changedCount,
        totalAbsDelta = state.totalAbsDelta,
        topDiscrepancies = state.topDiscrepancies,
        onConfirm = callbacks.onConfirmSubmit,
        onCancel = callbacks.onCancelSubmit,
    )
}

@Composable
private fun DualPaneBody(
    state: StockCountFormUiState,
    callbacks: StockCountFormCallbacks,
    history: List<StockCountHistoryEntry>,
) {
    BoxWithConstraints(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        val isWide = maxWidth >= 720.dp
        val rows = state.toFormRows()
        val emptySearching = state.query.isNotBlank()

        if (isWide) {
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Box(modifier = Modifier.weight(0.7f).fillMaxHeight()) {
                    TableCard {
                        StockCountFormTable(
                            rows = rows,
                            callbacks = callbacks,
                            emptySearching = emptySearching,
                        )
                    }
                }
                Box(modifier = Modifier.weight(0.3f).fillMaxHeight()) {
                    StockCountFormSummaryPanel(
                        state = state,
                        callbacks = callbacks,
                        history = history,
                    )
                }
            }
        } else {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                StockCountFormSummaryPanel(
                    state = state,
                    callbacks = callbacks,
                    history = history,
                )
                Box(modifier = Modifier.fillMaxWidth().weight(1f)) {
                    TableCard {
                        StockCountFormTable(
                            rows = rows,
                            callbacks = callbacks,
                            emptySearching = emptySearching,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TableCard(content: @Composable () -> Unit) {
    val t = pharmTokens
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(t.shapes.lg)
            .background(t.colors.surface)
            .border(1.dp, t.colors.borderSubtle, t.shapes.lg),
    ) {
        content()
    }
}

private fun StockCountFormUiState.toFormRows(): List<StockCountFormRow> {
    return filtered.map { drug ->
        val text = counts[drug.id].orEmpty()
        val counted = text.toIntOrNull()
        val delta = counted?.let { it - drug.stock }
        StockCountFormRow(
            drug = drug,
            countedText = text,
            counted = counted,
            delta = delta,
            highlighted = delta != null && delta != 0,
        )
    }
}

private val previewDrugs = listOf(
    Drug(
        id = "1", name = "พาราเซตามอล 500mg", genericName = "Paracetamol",
        type = "cur", strength = "500mg", barcode = "8851234567001",
        sellPrice = 2.0, costPrice = 1.2, stock = 480, minStock = 20, unit = "เม็ด", regNo = null,
    ),
    Drug(
        id = "2", name = "อะม็อกซีซิลลิน 500mg", genericName = "Amoxicillin",
        type = "cur", strength = "500mg", barcode = "8851234567002",
        sellPrice = 8.0, costPrice = 5.5, stock = 120, minStock = 30, unit = "แคปซูล", regNo = null,
    ),
    Drug(
        id = "3", name = "ไอบูโพรเฟน 400mg", genericName = "Ibuprofen",
        type = "cur", strength = "400mg", barcode = "8851234567003",
        sellPrice = 3.0, costPrice = 1.8, stock = 0, minStock = 20, unit = "เม็ด", regNo = null,
    ),
    Drug(
        id = "4", name = "ฟ้าทะลายโจร แคปซูล", genericName = "Andrographis",
        type = "herb", strength = "400mg", barcode = "8851234567004",
        sellPrice = 120.0, costPrice = 75.0, stock = 38, minStock = 10, unit = "ขวด", regNo = null,
    ),
    Drug(
        id = "5", name = "วิตามินซี 1000mg", genericName = "Vit C",
        type = "supp", strength = "1000mg", barcode = "8851234567005",
        sellPrice = 180.0, costPrice = 110.0, stock = 64, minStock = 5, unit = "ขวด", regNo = null,
    ),
)

private val previewHistory = listOf(
    StockCountHistoryEntry(countNo = "STC-260510-001", at = "10/05/26 17:42", itemsCount = 142, totalDelta = 8),
    StockCountHistoryEntry(countNo = "STC-260401-001", at = "01/04/26 18:20", itemsCount = 138, totalDelta = 2),
)

@Preview
@Composable
private fun StockCountFormContent_Loaded_Preview() {
    PharmacyTheme {
        StockCountFormContent(
            state = StockCountFormUiState(drugs = previewDrugs),
            history = previewHistory,
        )
    }
}

@Preview
@Composable
private fun StockCountFormContent_WithChanges_Preview() {
    PharmacyTheme {
        StockCountFormContent(
            state = StockCountFormUiState(
                drugs = previewDrugs,
                counts = mapOf("1" to "478", "3" to "0", "4" to "36", "5" to "64"),
                note = "ตรวจประจำเดือน พ.ค.",
            ),
            history = previewHistory,
        )
    }
}
