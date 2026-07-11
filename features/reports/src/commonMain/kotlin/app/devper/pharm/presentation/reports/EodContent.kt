package app.devper.pharm.presentation.reports

import app.devper.pharm.common.value.Money
import app.devper.pharm.common.value.Quantity

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.devper.pharm.domain.model.EodReport
import app.devper.pharm.domain.model.SaleSummary
import kotlinx.datetime.LocalDateTime
import app.devper.pharm.presentation.reports.components.EmptyEod
import app.devper.pharm.presentation.reports.components.EodBalanceCard
import app.devper.pharm.presentation.reports.components.EodBillRow
import app.devper.pharm.presentation.reports.components.EodBillsHeader
import app.devper.pharm.presentation.reports.components.EodClosedReceiptCard
import app.devper.pharm.presentation.reports.components.EodHeader
import app.devper.pharm.presentation.reports.i18n.localizeReports
import app.devper.pharm.ui.components.ErrorBottomSheet
import app.devper.pharm.ui.designsystem.PharmListSkeleton
import app.devper.pharm.ui.components.SubPageBar
import app.devper.pharm.ui.i18n.pharmStrings
import app.devper.pharm.ui.theme.PharmacyTheme
import app.devper.pharm.ui.theme.pharmTokens
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun EodContent(
    state: EodUiState,
    callbacks: EodCallbacks = EodCallbacks(),
    onBack: () -> Unit = {},
) {
    val report = state.report
    val t = pharmTokens
    val s = pharmStrings

    Column(modifier = Modifier.fillMaxSize().background(t.colors.bgPage)) {
        SubPageBar(title = s.reportsEodTitle, onBack = onBack)
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            EodHeader(
                date = state.date,
                loading = state.loading,
                closing = state.closing,
                closed = state.closed,
                hasReport = report != null,
                callbacks = callbacks,
            )

            when {
                state.loading && report == null -> PharmListSkeleton()

                report == null -> EmptyEod()

                else -> LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    if (state.pendingSyncCount > 0) item("pendingSync") {
                        EodPendingSyncBanner(count = state.pendingSyncCount)
                    }
                    item("summary") { EodSummaryCards(report) }
                    item("balance") { EodBalanceCard(report) }
                    if (state.closed) item("closed") {
                        EodClosedReceiptCard(
                            report = report,
                            template = state.closedTemplate,
                            onPrint = callbacks.onPrint,
                        )
                    }
                    item("bills") { EodBillsCard(report = report) }
                }
            }
        }
    }

    EodConfirmCloseModal(
        open = state.confirmClose,
        report = report,
        pendingSyncCount = state.pendingSyncCount,
        onConfirm = callbacks.onConfirmClose,
        onCancel = callbacks.onCancelClose,
    )

    ErrorBottomSheet(message = state.errorState?.localizeReports(s), onDismiss = callbacks.onDismissError)
}

@Composable
private fun EodBillsCard(report: EodReport) {
    val t = pharmTokens
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(t.colors.surface),
    ) {
        Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(t.colors.divider))
        Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp)) {
            EodBillsHeader(count = report.billCount)
        }
        Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(t.colors.divider))
        report.bills.forEachIndexed { index, bill ->
            EodBillRow(bill = bill)
            if (index < report.bills.lastIndex) {
                Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(t.colors.divider))
            }
        }
    }
}

private val sampleBills = listOf(
    SaleSummary("s1", "B260517-001", "นาย ก", Money(420.0), Money(0.0), LocalDateTime.parse("2026-05-17T09:15:00"), false),
    SaleSummary("s2", "B260517-002", "", Money(180.0), Money(20.0), LocalDateTime.parse("2026-05-17T10:45:00"), false),
    SaleSummary("s3", "B260517-003", "นาง ข", Money(950.0), Money(0.0), LocalDateTime.parse("2026-05-17T12:02:00"), true),
    SaleSummary("s4", "B260517-004", "ร้านยาฝั่งตรงข้าม", Money(2350.0), Money(50.0), LocalDateTime.parse("2026-05-17T13:24:00"), false),
)

private val sampleReport = EodReport(
    date = kotlinx.datetime.LocalDate.parse("2026-05-17"),
    billCount = 4,
    totalSales = 3850.0,
    totalDiscount = 70.0,
    totalReceived = 4000.0,
    totalChange = 150.0,
    netCash = 3850.0,
    bills = sampleBills,
)

private val unbalancedReport = sampleReport.copy(netCash = 3920.0)

@Preview
@Composable
private fun EodContent_Open_Preview() {
    PharmacyTheme { EodContent(state = EodUiState(date = "2026-05-17", report = sampleReport)) }
}

@Preview
@Composable
private fun EodContent_Closed_Preview() {
    PharmacyTheme { EodContent(state = EodUiState(date = "2026-05-17", report = sampleReport, closed = true)) }
}

@Preview
@Composable
private fun EodContent_Unbalanced_Preview() {
    PharmacyTheme { EodContent(state = EodUiState(date = "2026-05-17", report = unbalancedReport)) }
}

@Preview
@Composable
private fun EodContent_Confirm_Preview() {
    PharmacyTheme { EodContent(state = EodUiState(date = "2026-05-17", report = sampleReport, confirmClose = true)) }
}

@Preview
@Composable
private fun EodContent_Loading_Preview() {
    PharmacyTheme { EodContent(state = EodUiState(loading = true)) }
}

@Preview
@Composable
private fun EodContent_Empty_Preview() {
    PharmacyTheme { EodContent(state = EodUiState()) }
}
