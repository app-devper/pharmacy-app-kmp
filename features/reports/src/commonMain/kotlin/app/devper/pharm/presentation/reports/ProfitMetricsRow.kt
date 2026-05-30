package app.devper.pharm.presentation.reports

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import app.devper.pharm.domain.model.ProfitSummary
import app.devper.pharm.ui.designsystem.MetricCard
import app.devper.pharm.ui.designsystem.MetricCardRow
import app.devper.pharm.ui.designsystem.MetricTint
import app.devper.pharm.ui.theme.fmtBaht
import kotlin.math.roundToLong

@Composable
internal fun ProfitMetricsRow(summary: ProfitSummary?, modifier: Modifier = Modifier) {
    val revenue = summary?.revenue ?: 0.0
    val cost = summary?.cost ?: 0.0
    val profit = summary?.profit ?: 0.0
    val margin = summary?.margin ?: 0.0
    val marginText = "${(margin * 10).roundToLong() / 10.0}%"

    MetricCardRow(modifier = modifier) {
        MetricCard(
            label = "รายได้รวม",
            value = fmtBaht(revenue),
            sub = "ก่อนหักต้นทุน",
            tint = MetricTint.Blue,
            modifier = Modifier.weight(1f),
        )
        MetricCard(
            label = "ต้นทุนรวม",
            value = fmtBaht(cost),
            sub = "ตามล็อตที่ตัด",
            tint = MetricTint.Purple,
            modifier = Modifier.weight(1f),
        )
        MetricCard(
            label = "กำไรรวม",
            value = fmtBaht(profit),
            sub = "รายได้ - ต้นทุน",
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
