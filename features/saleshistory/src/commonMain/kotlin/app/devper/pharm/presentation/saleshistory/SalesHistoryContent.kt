package app.devper.pharm.presentation.saleshistory

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
import app.devper.pharm.domain.model.SaleSummary
import app.devper.pharm.ui.components.ErrorBottomSheet
import app.devper.pharm.ui.designsystem.PharmListResultLine
import app.devper.pharm.ui.designsystem.PharmListSkeleton
import app.devper.pharm.ui.theme.PharmacyTheme
import app.devper.pharm.ui.theme.pharmTokens
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun SalesHistoryContent(
    state: SalesHistoryUiState,
    callbacks: SalesHistoryCallbacks = SalesHistoryCallbacks(),
) {
    val t = pharmTokens
    val searching = state.from.isNotBlank() || state.to.isNotBlank() || state.query.isNotBlank()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(t.colors.bgPage),
    ) {
        SalesHistoryListToolbar(state = state, callbacks = callbacks)
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(16.dp)
                .clip(t.shapes.lg)
                .background(t.colors.surface)
                .border(1.dp, t.colors.borderSubtle, t.shapes.lg),
        ) {
            PharmListResultLine(
                total = state.sales.size,
                noun = "บิล",
                searching = searching,
                trailing = { SalesHistoryTotalStat(sales = state.sales) },
            )
            Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(t.colors.divider))

            when {
                state.loading && state.sales.isEmpty() -> PharmListSkeleton()
                else -> SalesHistoryTable(
                    sales = state.sales,
                    callbacks = callbacks,
                    emptySearching = searching,
                )
            }
        }
    }

    ErrorBottomSheet(message = state.error, onDismiss = callbacks.onDismissError)
}

private val sampleSales = listOf(
    SaleSummary(
        id = "1", billNo = "SC-260516-014", customerName = "คุณสมศรี ใจดี (VIP)",
        total = 1742.0, discount = 0.0, soldAt = "2026-05-17T14:42:00", voided = false,
    ),
    SaleSummary(
        id = "2", billNo = "SC-260516-013", customerName = "ลูกค้าทั่วไป",
        total = 120.0, discount = 0.0, soldAt = "2026-05-17T14:18:00", voided = false,
    ),
    SaleSummary(
        id = "3", billNo = "SC-260516-012", customerName = "นาย วรพล สุขสันต์",
        total = 892.0, discount = 0.0, soldAt = "2026-05-17T13:55:00", voided = false,
    ),
    SaleSummary(
        id = "4", billNo = "SC-260516-011", customerName = "ลูกค้าทั่วไป",
        total = 240.0, discount = 0.0, soldAt = "2026-05-17T13:30:00", voided = false,
    ),
    SaleSummary(
        id = "5", billNo = "SC-260516-010", customerName = "นาง พรรณี สวยงาม",
        total = 520.0, discount = 0.0, soldAt = "2026-05-17T12:45:00", voided = true,
    ),
    SaleSummary(
        id = "6", billNo = "SC-260516-009", customerName = "นาย เอกชัย สุภาพ",
        total = 95.0, discount = 0.0, soldAt = "2026-05-17T12:18:00", voided = false,
    ),
    SaleSummary(
        id = "7", billNo = "SC-260516-008", customerName = "ลูกค้าทั่วไป",
        total = 1318.0, discount = 0.0, soldAt = "2026-05-17T11:50:00", voided = false,
    ),
    SaleSummary(
        id = "8", billNo = "SC-260516-007", customerName = "คุณสมศรี ใจดี (VIP)",
        total = 160.0, discount = 0.0, soldAt = "2026-05-17T11:24:00", voided = false,
    ),
)

@Preview
@Composable
private fun SalesHistoryContent_Loaded_Preview() {
    PharmacyTheme {
        SalesHistoryContent(
            state = SalesHistoryUiState(
                sales = sampleSales,
                from = "2026-05-17",
                to = "2026-05-17",
            ),
        )
    }
}

@Preview
@Composable
private fun SalesHistoryContent_Empty_Preview() {
    PharmacyTheme {
        SalesHistoryContent(
            state = SalesHistoryUiState(
                sales = emptyList(),
                from = "2026-05-17",
                to = "2026-05-17",
            ),
        )
    }
}

@Preview
@Composable
private fun SalesHistoryContent_Searching_Preview() {
    PharmacyTheme {
        SalesHistoryContent(
            state = SalesHistoryUiState(
                sales = sampleSales.filter { it.customerName.contains("สมศรี") },
                from = "2026-05-17",
                to = "2026-05-17",
                query = "สมศรี",
            ),
        )
    }
}

@Preview
@Composable
private fun SalesHistoryContent_Loading_Preview() {
    PharmacyTheme {
        SalesHistoryContent(state = SalesHistoryUiState(loading = true))
    }
}
