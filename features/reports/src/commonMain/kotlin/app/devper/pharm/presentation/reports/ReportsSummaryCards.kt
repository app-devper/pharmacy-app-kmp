package app.devper.pharm.presentation.reports

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import app.devper.pharm.domain.model.ReportSummary
import app.devper.pharm.ui.designsystem.MetricCard
import app.devper.pharm.ui.designsystem.MetricCardRow
import app.devper.pharm.ui.designsystem.MetricTint
import app.devper.pharm.ui.i18n.pharmStrings
import app.devper.pharm.ui.theme.fmtBaht

@Composable
internal fun ReportsMetricsRow(
    summary: ReportSummary,
    monthProfit: Double?,
    modifier: Modifier = Modifier,
) {
    val s = pharmStrings
    MetricCardRow(modifier = modifier) {
        MetricCard(
            label = s.reportsMetricSalesToday,
            value = fmtBaht(summary.todaySales),
            sub = "${summary.todayBills} ${s.movementsCountNoun}",
        )
        MetricCard(
            label = s.reportsMetricSalesMonth,
            value = fmtBaht(summary.monthSales),
            sub = s.commonBaht,
        )
        MetricCard(
            label = s.reportsMetricProfitMonth,
            value = monthProfit?.let { fmtBaht(it) } ?: "—",
            sub = s.reportsMetricProfitMonthHint,
            tint = when {
                monthProfit == null -> MetricTint.Neutral
                monthProfit > 0.0 -> MetricTint.Success
                monthProfit < 0.0 -> MetricTint.Danger
                else -> MetricTint.Neutral
            },
        )
        MetricCard(
            label = s.reportsMetricStockValue,
            value = fmtBaht(summary.stockValue),
            sub = s.reportsMetricStockHint(summary.outStock, summary.lowStock),
            tint = when {
                summary.outStock > 0 -> MetricTint.Danger
                summary.lowStock > 0 -> MetricTint.Warning
                else -> MetricTint.Neutral
            },
        )
    }
}
