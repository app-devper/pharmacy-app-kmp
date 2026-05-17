package app.devper.pharm.ui.designsystem

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import app.devper.pharm.ui.theme.pharmTokens

data class PharmBarDatum(
    val label: String,
    val value: Double,
)

@Composable
fun PharmMiniBarChart(
    data: List<PharmBarDatum>,
    modifier: Modifier = Modifier,
    height: Dp = 96.dp,
    barColor: Color? = null,
    barRadius: Dp = 2.dp,
    barGap: Dp = 2.dp,
) {
    val t = pharmTokens
    val color = barColor ?: t.colors.accent
    val maxValue = (data.maxOfOrNull { it.value } ?: 0.0).coerceAtLeast(1.0)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height),
    ) {
        Canvas(modifier = Modifier.fillMaxWidth().height(height)) {
            if (data.isEmpty()) return@Canvas
            val gapPx = barGap.toPx()
            val barWidth = (size.width - gapPx * (data.size - 1)) / data.size
            val radius = CornerRadius(barRadius.toPx(), barRadius.toPx())

            data.forEachIndexed { index, datum ->
                val ratio = (datum.value / maxValue).toFloat().coerceIn(0f, 1f)
                val barHeight = size.height * ratio
                val x = index * (barWidth + gapPx)
                val y = size.height - barHeight
                drawRoundRect(
                    color = color,
                    topLeft = Offset(x, y),
                    size = Size(barWidth, barHeight),
                    cornerRadius = radius,
                )
            }
        }
    }
}

@Composable
fun PharmGroupedBarChart(
    revenue: List<PharmBarDatum>,
    cost: List<PharmBarDatum>,
    modifier: Modifier = Modifier,
    height: Dp = 96.dp,
    barGap: Dp = 4.dp,
    pairGap: Dp = 8.dp,
) {
    val t = pharmTokens
    val revenueColor = t.colors.accent
    val costColor = t.colors.warningFg
    val groupCount = minOf(revenue.size, cost.size)
    val combined = (revenue + cost)
    val maxValue = (combined.maxOfOrNull { it.value } ?: 0.0).coerceAtLeast(1.0)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height),
    ) {
        Canvas(modifier = Modifier.fillMaxWidth().height(height)) {
            if (groupCount == 0) return@Canvas
            val pairGapPx = pairGap.toPx()
            val barGapPx = barGap.toPx()
            val groupWidth = (size.width - pairGapPx * (groupCount - 1)) / groupCount
            val barWidth = (groupWidth - barGapPx) / 2f
            val radius = CornerRadius(2.dp.toPx(), 2.dp.toPx())

            for (i in 0 until groupCount) {
                val baseX = i * (groupWidth + pairGapPx)
                val rRatio = (revenue[i].value / maxValue).toFloat().coerceIn(0f, 1f)
                val cRatio = (cost[i].value / maxValue).toFloat().coerceIn(0f, 1f)
                val rHeight = size.height * rRatio
                val cHeight = size.height * cRatio

                drawRoundRect(
                    color = revenueColor,
                    topLeft = Offset(baseX, size.height - rHeight),
                    size = Size(barWidth, rHeight),
                    cornerRadius = radius,
                )
                drawRoundRect(
                    color = costColor,
                    topLeft = Offset(baseX + barWidth + barGapPx, size.height - cHeight),
                    size = Size(barWidth, cHeight),
                    cornerRadius = radius,
                )
            }
        }
    }
}
