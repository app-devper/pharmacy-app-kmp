package app.devper.pharm.presentation.saleshistory

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import app.devper.pharm.common.value.Money
import app.devper.pharm.common.value.Quantity
import app.devper.pharm.domain.model.SaleSummary
import app.devper.pharm.presentation.saleshistory.i18n.localizeSalesHistory
import app.devper.pharm.ui.components.ErrorBottomSheet
import app.devper.pharm.ui.components.unlessPageShowsError
import app.devper.pharm.ui.designsystem.PharmEmptyState
import app.devper.pharm.ui.designsystem.PharmErrorState
import app.devper.pharm.ui.designsystem.PharmIcons
import app.devper.pharm.ui.designsystem.PharmListResultLine
import app.devper.pharm.ui.designsystem.PharmListScaffold
import app.devper.pharm.ui.designsystem.PharmListSkeleton
import app.devper.pharm.ui.i18n.pharmStrings
import app.devper.pharm.ui.theme.PharmacyTheme
import kotlinx.datetime.LocalDateTime

@Composable
fun SalesHistoryContent(
    state: SalesHistoryUiState,
    callbacks: SalesHistoryCallbacks = SalesHistoryCallbacks(),
) {
    val pageIsEmpty = state.sales.isEmpty()
    val s = pharmStrings
    val searching = state.query.isNotBlank()

    PharmListScaffold(
        toolbar = { SalesHistoryListToolbar(state = state, callbacks = callbacks) },
        resultLine = {
            PharmListResultLine(
                total = state.sales.size,
                noun = s.salesHistoryCountNoun,
                searching = searching,
                trailing = { SalesHistoryTotalStat(sales = state.sales) },
            )
        },
    ) {
        when {
            state.loading && pageIsEmpty -> PharmListSkeleton(modifier = Modifier.fillMaxSize())
            state.errorState != null && pageIsEmpty ->
                PharmErrorState()
            pageIsEmpty -> PharmEmptyState(
                icon = if (searching) PharmIcons.Search else PharmIcons.SalesHistory,
                title = if (searching) s.salesHistoryEmptySearching else s.salesHistoryEmptyDateRange,
            )
            else -> SalesHistoryTable(
                sales = state.sales,
                callbacks = callbacks,
                emptySearching = searching,
            )
        }
    }

    ErrorBottomSheet(message = state.errorState.unlessPageShowsError(pageIsEmpty)?.localizeSalesHistory(pharmStrings), onDismiss = callbacks.onDismissError)
}

private val sampleSales = listOf(
    SaleSummary(
        id = "1", billNo = "SC-260516-014", customerName = "คุณสมศรี ใจดี (VIP)",
        total = Money(1742.0), discount = Money(0.0), soldAt = LocalDateTime.parse("2026-05-17T14:42:00"), voided = false,
    ),
    SaleSummary(
        id = "2", billNo = "SC-260516-013", customerName = "ลูกค้าทั่วไป",
        total = Money(120.0), discount = Money(0.0), soldAt = LocalDateTime.parse("2026-05-17T14:18:00"), voided = false,
    ),
    SaleSummary(
        id = "3", billNo = "SC-260516-012", customerName = "นาย วรพล สุขสันต์",
        total = Money(892.0), discount = Money(0.0), soldAt = LocalDateTime.parse("2026-05-17T13:55:00"), voided = false,
    ),
    SaleSummary(
        id = "4", billNo = "SC-260516-011", customerName = "ลูกค้าทั่วไป",
        total = Money(240.0), discount = Money(0.0), soldAt = LocalDateTime.parse("2026-05-17T13:30:00"), voided = false,
    ),
    SaleSummary(
        id = "5", billNo = "SC-260516-010", customerName = "นาง พรรณี สวยงาม",
        total = Money(520.0), discount = Money(0.0), soldAt = LocalDateTime.parse("2026-05-17T12:45:00"), voided = true,
    ),
    SaleSummary(
        id = "6", billNo = "SC-260516-009", customerName = "นาย เอกชัย สุภาพ",
        total = Money(95.0), discount = Money(0.0), soldAt = LocalDateTime.parse("2026-05-17T12:18:00"), voided = false,
    ),
    SaleSummary(
        id = "7", billNo = "SC-260516-008", customerName = "ลูกค้าทั่วไป",
        total = Money(1318.0), discount = Money(0.0), soldAt = LocalDateTime.parse("2026-05-17T11:50:00"), voided = false,
    ),
    SaleSummary(
        id = "8", billNo = "SC-260516-007", customerName = "คุณสมศรี ใจดี (VIP)",
        total = Money(160.0), discount = Money(0.0), soldAt = LocalDateTime.parse("2026-05-17T11:24:00"), voided = false,
    ),
)

@Preview
@Composable
private fun SalesHistoryContent_Loaded_Preview() {
    PharmacyTheme {
        SalesHistoryContent(
            state = SalesHistoryUiState(
                sales = sampleSales,
                dateRange = app.devper.pharm.ui.format.DateRangeFilter(from = "2026-05-17", to = "2026-05-17"),
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
                dateRange = app.devper.pharm.ui.format.DateRangeFilter(from = "2026-05-17", to = "2026-05-17"),
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
                dateRange = app.devper.pharm.ui.format.DateRangeFilter(from = "2026-05-17", to = "2026-05-17"),
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
