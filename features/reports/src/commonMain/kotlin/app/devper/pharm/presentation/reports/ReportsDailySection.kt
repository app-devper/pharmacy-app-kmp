package app.devper.pharm.presentation.reports

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Text
import app.devper.pharm.domain.model.DailySales
import app.devper.pharm.ui.designsystem.PharmBarDatum
import app.devper.pharm.ui.designsystem.PharmFormCard
import app.devper.pharm.ui.designsystem.PharmMiniBarChart
import app.devper.pharm.ui.i18n.pharmStrings
import app.devper.pharm.ui.theme.PharmText
import app.devper.pharm.ui.theme.fmtBaht
import app.devper.pharm.ui.theme.pharmTokens
import app.devper.pharm.ui.theme.tabular

private val DailyChartHeight = 128.dp

@Composable
internal fun ReportsDailyBarChart(daily: List<DailySales>, modifier: Modifier = Modifier) {
    val t = pharmTokens
    val data = daily.map { PharmBarDatum(label = dailyLabel(it.day), value = it.total) }
    val avg = if (daily.isNotEmpty()) daily.sumOf { it.total } / daily.size else 0.0

    val s = pharmStrings
    PharmFormCard(
        title = s.reportsSectionDailySales,
        modifier = modifier,
        trailing = {
            Text(
                text = s.reportsAvgPerDay(fmtBaht(avg)),
                style = PharmText.meta.tabular(),
            )
        },
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            if (data.isEmpty()) {
                ReportsChartEmpty(height = DailyChartHeight)
            } else {
                PharmMiniBarChart(
                    data = data,
                    height = DailyChartHeight,
                    barColor = t.colors.accent,
                    valueFormatter = { fmtBaht(it) },
                )
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
}

private fun dailyLabel(day: String): String {
    if (day.length < 10) return day
    return day.substring(8, 10) + "/" + day.substring(5, 7)
}
