package app.devper.pharm.presentation.reports

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import app.devper.pharm.domain.model.ReportSummary
import app.devper.pharm.ui.designsystem.MetricCard
import app.devper.pharm.ui.designsystem.MetricCardRow
import app.devper.pharm.ui.designsystem.MetricTint
import app.devper.pharm.ui.format.formatBahtCurrency

@Composable
internal fun ReportsMetricsRow(summary: ReportSummary, modifier: Modifier = Modifier) {
    MetricCardRow(modifier = modifier) {
        MetricCard(
            label = "ยอดขายวันนี้",
            value = formatBahtCurrency(summary.todaySales),
            sub = "${summary.todayBills} รายการ",
            tint = MetricTint.Blue,
            modifier = Modifier.weight(1f),
        )
        MetricCard(
            label = "ยอดขายเดือนนี้",
            value = formatBahtCurrency(summary.monthSales),
            sub = "บาท",
            tint = MetricTint.Indigo,
            modifier = Modifier.weight(1f),
        )
        MetricCard(
            label = "กำไรเดือนนี้ (ประมาณ)",
            value = formatBahtCurrency(estimatedMonthProfit(summary)),
            sub = "ประเมิน ~30% ของยอดขาย",
            tint = MetricTint.Green,
            modifier = Modifier.weight(1f),
        )
        MetricCard(
            label = "มูลค่าสต็อก",
            value = formatBahtCurrency(summary.stockValue),
            sub = "หมด ${summary.outStock} / ใกล้หมด ${summary.lowStock}",
            tint = MetricTint.Purple,
            modifier = Modifier.weight(1f),
        )
    }
}

private fun estimatedMonthProfit(summary: ReportSummary): Double =
    summary.monthSales * MONTH_PROFIT_RATIO

private const val MONTH_PROFIT_RATIO = 0.30
