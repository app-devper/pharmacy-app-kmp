package app.devper.pharm.presentation.reports

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.devper.pharm.domain.model.DrugProfit
import app.devper.pharm.domain.model.ProfitReport
import app.devper.pharm.domain.model.ProfitSummary
import app.devper.pharm.presentation.reports.i18n.localizeReports
import app.devper.pharm.ui.components.ErrorBottomSheet
import app.devper.pharm.ui.designsystem.PharmEmptyState
import app.devper.pharm.ui.designsystem.PharmErrorState
import app.devper.pharm.ui.designsystem.unlessPageShowsError
import app.devper.pharm.ui.designsystem.PharmIcons
import app.devper.pharm.ui.designsystem.PharmListResultLine
import app.devper.pharm.ui.designsystem.PharmListScaffold
import app.devper.pharm.ui.designsystem.PharmListSkeleton
import app.devper.pharm.ui.i18n.pharmStrings
import app.devper.pharm.ui.theme.PharmText
import app.devper.pharm.ui.theme.PharmacyTheme
import app.devper.pharm.ui.theme.pharmTokens

@Composable
fun ProfitContent(
    state: ProfitUiState,
    callbacks: ProfitCallbacks = ProfitCallbacks(),
) {
    val rows = state.sortedRows
    val totals = rows.takeIf { it.isNotEmpty() }?.let { buildTotals(it) }

    PharmListScaffold(
        metrics = { ProfitMetricsRow(summary = state.summary) },
        banner = if (state.missingCostCount > 0) {
            { MissingCostBanner(count = state.missingCostCount) }
        } else null,
        toolbar = { ProfitFilterBar(state = state, callbacks = callbacks) },
        resultLine = { PharmListResultLine(total = rows.size, noun = pharmStrings.movementsCountNoun) },
    ) {
        when {
            state.loading && state.report == null -> PharmListSkeleton()
            state.errorState != null && state.report == null -> PharmErrorState()
            rows.isEmpty() && state.report != null ->
                PharmEmptyState(
                    icon = PharmIcons.Profit,
                    title = pharmStrings.reportsSectionDailySalesEmpty,
                    subtitle = pharmStrings.reportsEodTryDifferentRange,
                )
            else -> ProfitTable(rows = rows, totals = totals)
        }
    }

    ErrorBottomSheet(message = state.errorState.unlessPageShowsError(state.report == null)?.localizeReports(pharmStrings), onDismiss = callbacks.onDismissError)
}

@Composable
private fun MissingCostBanner(count: Int) {
    val t = pharmTokens
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(t.shapes.lg)
            .background(t.colors.warningBg)
            .border(1.dp, t.colors.warningFg.copy(alpha = 0.35f), t.shapes.lg)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Icon(
            imageVector = PharmIcons.Warning,
            contentDescription = null,
            tint = t.colors.warningFg,
            modifier = Modifier.size(18.dp),
        )
        Text(
            text = pharmStrings.reportsProfitMissingCostBanner(count),
            style = PharmText.bodySm.copy(color = t.colors.warningFg, fontWeight = FontWeight.Medium),
        )
    }
}

private fun buildTotals(rows: List<DrugProfit>): ProfitTotals {
    val qty = rows.sumOf { it.qtySold }
    val revenue = rows.sumOf { it.revenue }
    val cost = rows.sumOf { it.cost }
    val profit = rows.sumOf { it.profit }
    val margin = if (revenue > 0.0) profit / revenue * 100.0 else 0.0
    return ProfitTotals(qty = qty, revenue = revenue, cost = cost, profit = profit, margin = margin)
}

private val sampleRows = listOf(
    DrugProfit("d1", "พาราเซตามอล 500mg",   qtySold = 380, revenue =  760.0, cost =  456.0, profit =  304.0, margin = 40.0),
    DrugProfit("d2", "อะม็อกซีซิลลิน 500mg", qtySold = 220, revenue = 1760.0, cost = 1210.0, profit =  550.0, margin = 31.3),
    DrugProfit("d3", "วิตามินซี 1000mg",      qtySold =  48, revenue = 8640.0, cost = 5280.0, profit = 3360.0, margin = 38.9),
    DrugProfit("d4", "ฟ้าทะลายโจร แคปซูล",   qtySold =  32, revenue = 3840.0, cost = 2400.0, profit = 1440.0, margin = 37.5),
    DrugProfit("d5", "ลอราทาดีน 10mg",        qtySold = 124, revenue =  496.0, cost =  310.0, profit =  186.0, margin = 37.5),
    DrugProfit("d6", "ออเมพราโซล 20mg",       qtySold =  64, revenue =  384.0, cost =  300.0, profit =   84.0, margin = 21.9),
    DrugProfit("d7", "ซาลบูทามอล MDI",        qtySold =   8, revenue =  760.0, cost =  720.0, profit =   40.0, margin =  5.3),
    DrugProfit("d8", "น้ำมันปลา 1000mg",       qtySold =   2, revenue =  700.0, cost =  700.0, profit =    0.0, margin =  0.0),
    DrugProfit("d9", "ขาดทุนตัวอย่าง",         qtySold =   3, revenue =  300.0, cost =  450.0, profit = -150.0, margin = -50.0),
)

private val sampleSummary = ProfitSummary(
    revenue = sampleRows.sumOf { it.revenue },
    cost = sampleRows.sumOf { it.cost },
    profit = sampleRows.sumOf { it.profit },
    margin = run {
        val r = sampleRows.sumOf { it.revenue }
        val p = sampleRows.sumOf { it.profit }
        if (r > 0.0) p / r * 100.0 else 0.0
    },
    bills = 124,
)

@Preview
@Composable
private fun ProfitContent_Loaded_Preview() {
    PharmacyTheme {
        ProfitContent(
            state = ProfitUiState(
                dateRange = app.devper.pharm.ui.format.DateRangeFilter(from = "2026-05-01", to = "2026-05-17"),
                report = ProfitReport(summary = sampleSummary, byDrug = sampleRows),
            ),
        )
    }
}

@Preview
@Composable
private fun ProfitContent_Empty_Preview() {
    PharmacyTheme {
        ProfitContent(
            state = ProfitUiState(
                dateRange = app.devper.pharm.ui.format.DateRangeFilter(from = "2026-05-01", to = "2026-05-17"),
                report = ProfitReport(
                    summary = ProfitSummary(0.0, 0.0, 0.0, 0.0, 0),
                    byDrug = emptyList(),
                ),
            ),
        )
    }
}

@Preview
@Composable
private fun ProfitContent_Loading_Preview() {
    PharmacyTheme {
        ProfitContent(state = ProfitUiState(loading = true))
    }
}
