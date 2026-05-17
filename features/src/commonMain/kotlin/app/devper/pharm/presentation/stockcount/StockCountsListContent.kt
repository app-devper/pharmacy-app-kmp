package app.devper.pharm.presentation.stockcount

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import app.devper.pharm.domain.model.StockCount
import app.devper.pharm.domain.model.StockCountLine
import app.devper.pharm.ui.components.ErrorBottomSheet
import app.devper.pharm.ui.theme.PharmText
import app.devper.pharm.ui.theme.PharmacyTheme
import app.devper.pharm.ui.theme.pharmTokens
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun StockCountsListContent(
    state: StockCountsListUiState,
    callbacks: StockCountsListCallbacks = StockCountsListCallbacks(),
) {
    val t = pharmTokens
    val visible = state.filtered
    val searching = state.query.isNotBlank()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(t.colors.bgPage)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .clip(t.shapes.lg)
                .background(t.colors.surface)
                .border(1.dp, t.colors.borderSubtle, t.shapes.lg),
        ) {
            StockCountsListToolbar(query = state.query, callbacks = callbacks)
            Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(t.colors.divider))
            StockCountsResultLine(
                visible = visible.size,
                total = state.counts.size,
                searching = searching,
            )
            Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(t.colors.divider))

            when {
                state.loading && state.counts.isEmpty() ->
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = t.colors.accent)
                    }
                else -> StockCountsListTable(
                    counts = visible,
                    callbacks = callbacks,
                    emptySearching = searching,
                )
            }
        }
    }

    ErrorBottomSheet(message = state.error, onDismiss = callbacks.onDismissError)
}

@Composable
private fun StockCountsResultLine(visible: Int, total: Int, searching: Boolean) {
    val t = pharmTokens
    val text = if (searching) "พบ $visible รอบ จากทั้งหมด $total"
    else "ทั้งหมด $total รอบ"
    Text(
        text = text,
        style = PharmText.micro.copy(color = t.colors.fg3),
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
    )
}

private val sampleCounts = listOf(
    StockCount(
        id = "1",
        countNo = "STC-260510-001",
        note = "ตรวจประจำเดือน พ.ค.",
        createdAt = "2026-05-10T17:42:00",
        items = listOf(
            StockCountLine(drugId = "d1", drugName = "พาราเซตามอล 500mg", unit = "เม็ด", systemStock = 480, counted = 478, delta = -2),
            StockCountLine(drugId = "d2", drugName = "อะม็อกซีซิลลิน 500mg", unit = "แคปซูล", systemStock = 120, counted = 120, delta = 0),
            StockCountLine(drugId = "d3", drugName = "ไอบูโพรเฟน 400mg", unit = "เม็ด", systemStock = 60, counted = 54, delta = -6),
        ),
    ),
    StockCount(
        id = "2",
        countNo = "STC-260401-001",
        note = "",
        createdAt = "2026-04-01T18:20:00",
        items = listOf(
            StockCountLine(drugId = "d1", drugName = "พาราเซตามอล 500mg", unit = "เม็ด", systemStock = 500, counted = 502, delta = 2),
        ),
    ),
    StockCount(
        id = "3",
        countNo = "STC-260315-001",
        note = "ก่อนเปิดร้านสาขาใหม่",
        createdAt = "2026-03-15T08:05:00",
        items = listOf(
            StockCountLine(drugId = "d4", drugName = "วิตามินซี 1000mg", unit = "ขวด", systemStock = 64, counted = 64, delta = 0),
            StockCountLine(drugId = "d5", drugName = "ลอราทาดีน 10mg", unit = "เม็ด", systemStock = 240, counted = 240, delta = 0),
        ),
    ),
)

@Preview
@Composable
private fun StockCountsListContent_Loaded_Preview() {
    PharmacyTheme {
        StockCountsListContent(state = StockCountsListUiState(counts = sampleCounts))
    }
}

@Preview
@Composable
private fun StockCountsListContent_Empty_Preview() {
    PharmacyTheme {
        StockCountsListContent(state = StockCountsListUiState(counts = emptyList()))
    }
}

@Preview
@Composable
private fun StockCountsListContent_Searching_Preview() {
    PharmacyTheme {
        StockCountsListContent(state = StockCountsListUiState(counts = sampleCounts, query = "STC-260510"))
    }
}

@Preview
@Composable
private fun StockCountsListContent_Loading_Preview() {
    PharmacyTheme {
        StockCountsListContent(state = StockCountsListUiState(loading = true))
    }
}
