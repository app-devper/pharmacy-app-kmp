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
        )
        MetricCard(
            label = s.reportsProfitCost,
            value = fmtBaht(cost),
            sub = s.reportsCostBasis,
        )
        MetricCard(
            label = s.reportsProfitTotal,
            value = fmtBaht(profit),
            sub = s.reportsRevenueMinusCost,
            tint = when {
                profit > 0.0 -> MetricTint.Success
                profit < 0.0 -> MetricTint.Danger
                else -> MetricTint.Neutral
            },
        )
        MetricCard(
            label = pharmStrings.reportsAvgMargin,
            value = marginText,
            sub = pharmStrings.reportsAvgMarginHint,
        )
    }
}
