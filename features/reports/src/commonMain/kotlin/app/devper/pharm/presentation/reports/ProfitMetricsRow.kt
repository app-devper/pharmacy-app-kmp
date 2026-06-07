package app.devper.pharm.presentation.reports

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import app.devper.pharm.domain.model.ProfitSummary
import app.devper.pharm.ui.designsystem.MetricCard
import app.devper.pharm.ui.designsystem.MetricCardRow
import app.devper.pharm.ui.designsystem.MetricTint
import app.devper.pharm.ui.i18n.pharmStrings
import app.devper.pharm.ui.theme.fmtBaht
import kotlin.math.roundToLong

@Composable
internal fun ProfitMetricsRow(summary: ProfitSummary?, modifier: Modifier = Modifier) {
    val revenue = summary?.revenue ?: 0.0
    val cost = summary?.cost ?: 0.0
    val profit = summary?.profit ?: 0.0
    val margin = summary?.margin ?: 0.0
    val marginText = "${(margin * 10).roundToLong() / 10.0}%"

    val s = pharmStrings
    MetricCardRow(modifier = modifier) {
        MetricCard(
            label = s.reportsProfitRevenue,
            value = fmtBaht(revenue),
            sub = s.reportsProfitBeforeCost,
            tint = MetricTint.Blue,
            modifier = Modifier.weight(1f),
        )
        MetricCard(
            label = s.reportsProfitCost,
            value = fmtBaht(cost),
            sub = s.reportsCostBasis,
            tint = MetricTint.Purple,
            modifier = Modifier.weight(1f),
        )
        MetricCard(
            label = s.reportsProfitTotal,
            value = fmtBaht(profit),
            sub = s.reportsRevenueMinusCost,
            tint = MetricTint.Green,
            modifier = Modifier.weight(1f),
        )
        MetricCard(
            label = "Margin เฉลี่ย",
            value = marginText,
            sub = "weighted by revenue",
            tint = MetricTint.Indigo,
            modifier = Modifier.weight(1f),
        )
    }
}
