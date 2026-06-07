package app.devper.pharm.presentation.reports

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.devper.pharm.domain.model.Dashboard
import app.devper.pharm.domain.model.DailySales
import app.devper.pharm.domain.model.MonthlySales
import app.devper.pharm.domain.model.ReportSummary
import app.devper.pharm.domain.model.SaleSummary
import kotlinx.datetime.LocalDateTime
import app.devper.pharm.domain.model.SlowDrug
import app.devper.pharm.domain.model.TopDrug
import app.devper.pharm.ui.components.ErrorBottomSheet
import app.devper.pharm.ui.designsystem.PharmButton
import app.devper.pharm.ui.designsystem.PharmButtonSize
import app.devper.pharm.ui.designsystem.PharmListToolbar
import app.devper.pharm.ui.theme.PharmacyTheme
import app.devper.pharm.ui.theme.pharmTokens
import androidx.compose.ui.tooling.preview.Preview
import app.devper.pharm.ui.designsystem.PharmListSkeleton

@Composable
fun ReportsContent(
    state: ReportsUiState,
    callbacks: ReportsCallbacks = ReportsCallbacks(),
) {
    val t = pharmTokens
    BoxWithConstraints(
        modifier = Modifier.fillMaxSize().background(t.colors.bgPage),
        contentAlignment = Alignment.TopCenter,
    ) {
        val contentModifier = if (maxWidth >= 1000.dp) Modifier.widthIn(max = 1040.dp).fillMaxSize()
        else Modifier.fillMaxSize()
        val stackTopAndSlow = maxWidth < 700.dp

        Column(modifier = contentModifier) {
            PharmListToolbar(
                title = "รายงานสรุป",
                subtitle = "ภาพรวมยอดขาย สต็อก และสินค้าขายดี",
                actions = {
                    PharmButton(
                        label = "ปิดรอบ EOD",
                        onClick = callbacks.onCloseEod,
                        size = PharmButtonSize.Md,
                    )
                },
            )

            if (state.loading && state.dashboard == null) {
                PharmListSkeleton(modifier = Modifier.fillMaxSize())
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    state.dashboard?.summary?.let {
                        item("metrics") { ReportsMetricsRow(summary = it) }
                    }
                    item("window") {
                        ReportsWindowChips(state = state, onSelectWindow = callbacks.onSelectWindow)
                    }
                    state.dashboard?.daily?.let { daily ->
                        item("daily") { ReportsDailyBarChart(daily = daily) }
                    }
                    state.dashboard?.monthly?.let { monthly ->
                        item("monthly") { ReportsMonthlyGroupedBars(monthly = monthly) }
                    }
                    item("top-and-slow") {
                        if (stackTopAndSlow) {
                            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                                ReportsTopDrugsSection(rows = state.topDrugs, modifier = Modifier.fillMaxWidth())
                                ReportsSlowDrugsSection(rows = state.slowDrugs, modifier = Modifier.fillMaxWidth())
                            }
                        } else {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                            ) {
                                ReportsTopDrugsSection(rows = state.topDrugs, modifier = Modifier.weight(1f))
                                ReportsSlowDrugsSection(rows = state.slowDrugs, modifier = Modifier.weight(1f))
                            }
                        }
                    }
                    state.dashboard?.recentSales?.let { recent ->
                        item("recent") { ReportsRecentSalesSection(recent = recent) }
                    }
                }
            }
        }
    }

    ErrorBottomSheet(message = state.error, onDismiss = callbacks.onDismissError)
}

private val previewSummary = ReportSummary(
    todaySales = 18420.0,
    todayBills = 42,
    monthSales = 412000.0,
    stockValue = 1240000.0,
    lowStock = 12,
    outStock = 3,
)

private val previewDaily = listOf(
    DailySales("2026-05-10", 13800.0),
    DailySales("2026-05-11", 9200.0),
    DailySales("2026-05-12", 16400.0),
    DailySales("2026-05-13", 11600.0),
    DailySales("2026-05-14", 14200.0),
    DailySales("2026-05-15", 18900.0),
    DailySales("2026-05-16", 8420.0),
    DailySales("2026-05-17", 18420.0),
)

private val previewMonthly = listOf(
    MonthlySales("2025-06", 78000.0, 50700.0, 27300.0),
    MonthlySales("2025-07", 92000.0, 59800.0, 32200.0),
    MonthlySales("2025-08", 88000.0, 57200.0, 30800.0),
    MonthlySales("2025-09", 105000.0, 68250.0, 36750.0),
    MonthlySales("2025-10", 96000.0, 62400.0, 33600.0),
    MonthlySales("2025-11", 70000.0, 45500.0, 24500.0),
    MonthlySales("2025-12", 85000.0, 55250.0, 29750.0),
    MonthlySales("2026-01", 110000.0, 71500.0, 38500.0),
    MonthlySales("2026-02", 102000.0, 66300.0, 35700.0),
    MonthlySales("2026-03", 90000.0, 58500.0, 31500.0),
    MonthlySales("2026-04", 86000.0, 55900.0, 30100.0),
    MonthlySales("2026-05", 108000.0, 70200.0, 37800.0),
)

private val previewRecent = listOf(
    SaleSummary("s1", "SC-260516-014", "คุณสมศรี ใจดี", 1742.0, 0.0, LocalDateTime.parse("2026-05-17T14:42:00"), false),
    SaleSummary("s2", "SC-260516-013", "", 120.0, 0.0, LocalDateTime.parse("2026-05-17T14:18:00"), false),
    SaleSummary("s3", "SC-260516-012", "นาย วรพล สุขสันต์", 892.0, 0.0, LocalDateTime.parse("2026-05-17T13:55:00"), false),
    SaleSummary("s4", "SC-260516-011", "", 240.0, 0.0, LocalDateTime.parse("2026-05-17T13:30:00"), false),
    SaleSummary("s5", "SC-260516-010", "นาง พรรณี สวยงาม", 520.0, 0.0, LocalDateTime.parse("2026-05-17T12:45:00"), true),
)

private val previewTop = listOf(
    TopDrug("d1", "พาราเซตามอล 500mg", 380, 7600.0),
    TopDrug("d2", "อะม็อกซีซิลลิน 500mg", 220, 17600.0),
    TopDrug("d3", "วิตามินซี 1000mg", 48, 8640.0),
    TopDrug("d4", "ฟ้าทะลายโจร แคปซูล", 32, 3840.0),
    TopDrug("d5", "ลอราทาดีน 10mg", 124, 4960.0),
)

private val previewSlow = listOf(
    SlowDrug("d10", "น้ำมันปลา 1000mg", 0, "ขวด"),
    SlowDrug("d11", "ยาธาตุน้ำขาว", 2, "ขวด"),
    SlowDrug("d12", "ออเมพราโซล 20mg", 3, "เม็ด"),
)

@Preview
@Composable
private fun ReportsContent_Loaded_Preview() {
    PharmacyTheme {
        ReportsContent(
            state = ReportsUiState(
                dashboard = Dashboard(
                    summary = previewSummary,
                    daily = previewDaily,
                    monthly = previewMonthly,
                    recentSales = previewRecent,
                ),
                topDrugs = previewTop,
                slowDrugs = previewSlow,
            ),
            callbacks = ReportsCallbacks(),
        )
    }
}

@Preview
@Composable
private fun ReportsContent_Loading_Preview() {
    PharmacyTheme {
        ReportsContent(
            state = ReportsUiState(loading = true),
            callbacks = ReportsCallbacks(),
        )
    }
}

@Preview
@Composable
private fun ReportsContent_Empty_Preview() {
    PharmacyTheme {
        ReportsContent(
            state = ReportsUiState(
                dashboard = Dashboard(
                    summary = previewSummary.copy(todaySales = 0.0, todayBills = 0),
                    daily = emptyList(),
                    monthly = emptyList(),
                    recentSales = emptyList(),
                ),
            ),
            callbacks = ReportsCallbacks(),
        )
    }
}

@Preview
@Composable
private fun ReportsContent_NarrowWindow_Preview() {
    PharmacyTheme {
        ReportsContent(
            state = ReportsUiState(
                window = DashboardWindow.Last30,
                dashboard = Dashboard(
                    summary = previewSummary,
                    daily = previewDaily,
                    monthly = previewMonthly,
                    recentSales = previewRecent,
                ),
                topDrugs = previewTop,
                slowDrugs = previewSlow,
            ),
            callbacks = ReportsCallbacks(),
        )
    }
}
