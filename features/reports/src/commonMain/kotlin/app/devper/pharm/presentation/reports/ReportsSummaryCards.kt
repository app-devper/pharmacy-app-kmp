package app.devper.pharm.presentation.reports

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import app.devper.pharm.domain.model.ReportSummary
import app.devper.pharm.ui.designsystem.MetricCard
import app.devper.pharm.ui.designsystem.MetricCardRow
import app.devper.pharm.ui.designsystem.MetricTint
import app.devper.pharm.ui.format.formatBahtCurrency
import app.devper.pharm.ui.i18n.pharmStrings

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
            value = formatBahtCurrency(summary.todaySales),
            sub = "${summary.todayBills} ${s.movementsCountNoun}",
            tint = MetricTint.Blue,
            modifier = Modifier.weight(1f),
        )
        MetricCard(
            label = s.reportsMetricSalesMonth,
            value = formatBahtCurrency(summary.monthSales),
            sub = s.commonBaht,
            tint = MetricTint.Indigo,
            modifier = Modifier.weight(1f),
        )
        MetricCard(
            label = s.reportsMetricProfitMonth,
            value = monthProfit?.let { formatBahtCurrency(it) } ?: "—",
            sub = s.reportsMetricProfitMonthHint,
            tint = MetricTint.Green,
            modifier = Modifier.weight(1f),
        )
        MetricCard(
            label = s.reportsMetricStockValue,
            value = formatBahtCurrency(summary.stockValue),
            sub = s.reportsMetricStockHint(summary.outStock, summary.lowStock),
            tint = MetricTint.Purple,
            modifier = Modifier.weight(1f),
        )
    }
}
