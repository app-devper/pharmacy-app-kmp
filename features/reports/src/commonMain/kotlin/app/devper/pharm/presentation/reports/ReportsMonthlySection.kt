package app.devper.pharm.presentation.reports

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import app.devper.pharm.ui.designsystem.PharmGroupedBarChart
import app.devper.pharm.ui.format.formatBahtCurrency
import app.devper.pharm.ui.theme.PharmText
import app.devper.pharm.ui.theme.pharmTokens

@Composable
internal fun ReportsMonthlyGroupedBars(monthly: List<MonthlySales>, modifier: Modifier = Modifier) {
    val t = pharmTokens
    val recent = monthly.takeLast(12)
    val revenueData = recent.map { PharmBarDatum(label = monthLabel(it.month), value = it.revenue) }
    val costData = recent.map { PharmBarDatum(label = monthLabel(it.month), value = it.cost) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(t.shapes.lg)
            .background(t.colors.surface, t.shapes.lg)
            .border(1.dp, t.colors.borderSubtle, t.shapes.lg)
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "รายได้ vs ต้นทุน — รายเดือน",
                style = PharmText.h3,
                modifier = Modifier.weight(1f),
            )
            Text(text = "${recent.size} เดือนล่าสุด", style = PharmText.meta)
        }
        PharmGroupedBarChart(
            revenue = revenueData,
            cost = costData,
            height = 144.dp,
            valueFormatter = { formatBahtCurrency(it) },
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            LegendDot(color = t.colors.accent, label = "รายได้")
            LegendDot(color = t.colors.warningFg, label = "ต้นทุน")
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
