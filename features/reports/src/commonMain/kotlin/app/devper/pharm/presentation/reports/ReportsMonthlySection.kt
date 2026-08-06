package app.devper.pharm.presentation.reports

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import app.devper.pharm.domain.model.MonthlySales
import app.devper.pharm.ui.designsystem.PharmBarDatum
import app.devper.pharm.ui.designsystem.PharmFormCard
import app.devper.pharm.ui.designsystem.PharmGroupedBarChart
import app.devper.pharm.ui.i18n.pharmStrings
import app.devper.pharm.ui.theme.PharmText
import app.devper.pharm.ui.theme.fmtBaht
import app.devper.pharm.ui.theme.pharmTokens

private val MonthlyChartHeight = 144.dp

@Composable
internal fun ReportsMonthlyGroupedBars(monthly: List<MonthlySales>, modifier: Modifier = Modifier) {
    val t = pharmTokens
    val recent = monthly.takeLast(12)
    val revenueData = recent.map { PharmBarDatum(label = monthLabel(it.month), value = it.revenue) }
    val costData = recent.map { PharmBarDatum(label = monthLabel(it.month), value = it.cost) }

    val s = pharmStrings
    PharmFormCard(
        title = s.reportsSectionMonthly,
        modifier = modifier,
        trailing = {
            Text(text = "${recent.size} ${s.reportsRangeThisMonth}", style = PharmText.meta)
        },
    ) {
        if (recent.isEmpty()) {
            ReportsChartEmpty(height = MonthlyChartHeight)
        } else {
            PharmGroupedBarChart(
                revenue = revenueData,
                cost = costData,
                height = MonthlyChartHeight,
                valueFormatter = { fmtBaht(it) },
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                LegendDot(color = t.colors.accent, label = pharmStrings.reportsHeaderRevenue)
                LegendDot(color = t.colors.warningFg, label = pharmStrings.reportsHeaderCost)
            }
        }
    }
}

@Composable
private fun LegendDot(color: Color, label: String) {
    val t = pharmTokens
    Row(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(color),
        )
        Text(text = label, style = PharmText.meta.copy(color = t.colors.fg2))
    }
}

private fun monthLabel(month: String): String {
    if (month.length < 7) return month
    return month.substring(5, 7) + "/" + month.substring(2, 4)
}
