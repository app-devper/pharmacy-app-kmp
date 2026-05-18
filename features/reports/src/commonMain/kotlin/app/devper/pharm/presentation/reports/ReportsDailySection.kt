package app.devper.pharm.presentation.reports

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Text
import app.devper.pharm.domain.model.DailySales
import app.devper.pharm.ui.designsystem.PharmBarDatum
import app.devper.pharm.ui.designsystem.PharmMiniBarChart
import app.devper.pharm.ui.format.formatBaht
import app.devper.pharm.ui.theme.PharmText
import app.devper.pharm.ui.theme.pharmTokens
import app.devper.pharm.ui.theme.tabular

@Composable
internal fun ReportsDailyBarChart(daily: List<DailySales>, modifier: Modifier = Modifier) {
    val t = pharmTokens
    val data = daily.map { PharmBarDatum(label = dailyLabel(it.day), value = it.total) }
    val avg = if (daily.isNotEmpty()) daily.sumOf { it.total } / daily.size else 0.0

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
            Text(text = "ยอดขายรายวัน", style = PharmText.h3, modifier = Modifier.weight(1f))
            Text(
                text = "เฉลี่ย ฿${formatBaht(avg)}/วัน",
                style = PharmText.meta.tabular(),
            )
        }
        PharmMiniBarChart(
            data = data,
            height = 128.dp,
            barColor = t.colors.accent,
            valueFormatter = { "฿${formatBaht(it)}" },
        )
        if (data.isNotEmpty()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                data.forEach { d ->
                    Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                        Text(
                            text = d.label,
                            style = PharmText.micro.tabular().copy(textAlign = TextAlign.Center),
                        )
                    }
                }
            }
        }
    }
}

private fun dailyLabel(day: String): String {
    if (day.length < 10) return day
    return day.substring(8, 10) + "/" + day.substring(5, 7)
}
