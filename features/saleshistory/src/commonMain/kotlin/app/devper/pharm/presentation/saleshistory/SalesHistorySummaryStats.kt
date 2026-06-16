package app.devper.pharm.presentation.saleshistory

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import app.devper.pharm.domain.model.SaleSummary
import app.devper.pharm.ui.designsystem.MetricCard
import app.devper.pharm.ui.designsystem.MetricCardRow
import app.devper.pharm.ui.designsystem.MetricTint
import app.devper.pharm.ui.i18n.pharmStrings
import app.devper.pharm.ui.theme.fmtBaht

@Composable
internal fun SalesHistoryMetricCards(
    sales: List<SaleSummary>,
    modifier: Modifier = Modifier,
) {
    val s = pharmStrings
    val valid = sales.filterNot { it.voided }
    val net = valid.sumOf { it.total.amount }
    val billCount = valid.size
    val avg = if (billCount > 0) net / billCount else 0.0
    val voided = sales.count { it.voided }

    MetricCardRow(modifier = modifier) {
        MetricCard(
            label = s.salesHistoryMetricNetSales,
            value = fmtBaht(net),
            sub = "$billCount ${s.salesHistoryCountNoun}",
            tint = MetricTint.Blue,
            modifier = Modifier.weight(1f),
        )
        MetricCard(
            label = s.salesHistoryMetricBills,
            value = billCount.toString(),
            tint = MetricTint.Indigo,
            modifier = Modifier.weight(1f),
        )
        MetricCard(
            label = s.salesHistoryMetricAvg,
            value = fmtBaht(avg),
            tint = MetricTint.Green,
            modifier = Modifier.weight(1f),
        )
        MetricCard(
            label = s.salesHistoryMetricVoided,
            value = voided.toString(),
            tint = MetricTint.Purple,
            modifier = Modifier.weight(1f),
        )
    }
}
