package app.devper.pharm.presentation.reports

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.devper.pharm.domain.model.ReportSummary
import app.devper.pharm.ui.designsystem.MetricCard
import app.devper.pharm.ui.designsystem.MetricTint
import app.devper.pharm.ui.format.formatBahtCurrency

@Composable
internal fun ReportsMetricsRow(summary: ReportSummary, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
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
            label = "กำไรเดือนนี้",
            value = formatBahtCurrency(estimatedMonthProfit(summary)),
            sub = "รายได้ - ต้นทุน",
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
