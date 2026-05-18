package app.devper.pharm.ui.designsystem

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import app.devper.pharm.ui.theme.PharmText
import app.devper.pharm.ui.theme.pharmTokens
import kotlinx.coroutines.delay

data class PharmBarDatum(
    val label: String,
    val value: Double,
)

private const val TOOLTIP_AUTO_DISMISS_MS: Long = 3500L

@Composable
fun PharmMiniBarChart(
    data: List<PharmBarDatum>,
    modifier: Modifier = Modifier,
    height: Dp = 96.dp,
    barColor: Color? = null,
    barRadius: Dp = 2.dp,
    barGap: Dp = 2.dp,
    valueFormatter: (Double) -> String = { it.toString() },
) {
    val t = pharmTokens
    val color = barColor ?: t.colors.accent
    val maxValue = (data.maxOfOrNull { it.value } ?: 0.0).coerceAtLeast(1.0)
    var selected by remember(data) { mutableStateOf<Int?>(null) }

    LaunchedEffect(selected) {
        if (selected != null) {
            delay(TOOLTIP_AUTO_DISMISS_MS)
            selected = null
        }
    }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .height(height),
    ) {
        val density = LocalDensity.current
        val widthPx = with(density) { maxWidth.toPx() }
        val heightPx = with(density) { height.toPx() }
        val gapPx = with(density) { barGap.toPx() }
        val barWidthPx = if (data.isEmpty()) 0f else (widthPx - gapPx * (data.size - 1)) / data.size

        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(data) {
                    detectTapGestures { offset ->
                        if (data.isEmpty() || barWidthPx <= 0f) return@detectTapGestures
                        val index = (offset.x / (barWidthPx + gapPx)).toInt().coerceIn(0, data.lastIndex)
                        selected = if (selected == index) null else index
                    }
                },
        ) {
            if (data.isEmpty()) return@Canvas
            val radius = CornerRadius(barRadius.toPx(), barRadius.toPx())

            data.forEachIndexed { index, datum ->
                val ratio = (datum.value / maxValue).toFloat().coerceIn(0f, 1f)
                val barHeight = size.height * ratio
                val x = index * (barWidthPx + gapPx)
                val y = size.height - barHeight
                val fill = if (selected == index) color else color.copy(alpha = if (selected == null) 1f else 0.55f)
                drawRoundRect(
                    color = fill,
                    topLeft = Offset(x, y),
                    size = Size(barWidthPx, barHeight),
                    cornerRadius = radius,
                )
            }
        }

        selected?.let { idx ->
            val datum = data[idx]
            val ratio = (datum.value / maxValue).toFloat().coerceIn(0f, 1f)
            val barTopPx = heightPx - heightPx * ratio
            val barCenterPx = idx * (barWidthPx + gapPx) + barWidthPx / 2f
            PharmBarTooltip(
                label = datum.label,
                value = valueFormatter(datum.value),
                anchorXPx = barCenterPx,
                anchorYPx = barTopPx,
                hostWidthPx = widthPx,
            )
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
    valueFormatter: (Double) -> String = { it.toString() },
    revenueLegend: String = "รายได้",
    costLegend: String = "ต้นทุน",
) {
    val t = pharmTokens
    val revenueColor = t.colors.accent
    val costColor = t.colors.warningFg
    val groupCount = minOf(revenue.size, cost.size)
    val combined = (revenue + cost)
    val maxValue = (combined.maxOfOrNull { it.value } ?: 0.0).coerceAtLeast(1.0)
    var selected by remember(revenue, cost) { mutableStateOf<Int?>(null) }

    LaunchedEffect(selected) {
        if (selected != null) {
            delay(TOOLTIP_AUTO_DISMISS_MS)
            selected = null
        }
    }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .height(height),
    ) {
        val density = LocalDensity.current
        val widthPx = with(density) { maxWidth.toPx() }
        val heightPx = with(density) { height.toPx() }
        val pairGapPx = with(density) { pairGap.toPx() }
        val barGapPx = with(density) { barGap.toPx() }
        val groupWidthPx = if (groupCount == 0) 0f else (widthPx - pairGapPx * (groupCount - 1)) / groupCount
        val barWidthPx = if (groupCount == 0) 0f else (groupWidthPx - barGapPx) / 2f

        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(revenue, cost) {
                    detectTapGestures { offset ->
                        if (groupCount == 0 || groupWidthPx <= 0f) return@detectTapGestures
                        val index = (offset.x / (groupWidthPx + pairGapPx)).toInt().coerceIn(0, groupCount - 1)
                        selected = if (selected == index) null else index
                    }
                },
        ) {
            if (groupCount == 0) return@Canvas
            val radius = CornerRadius(2.dp.toPx(), 2.dp.toPx())

            for (i in 0 until groupCount) {
                val baseX = i * (groupWidthPx + pairGapPx)
                val rRatio = (revenue[i].value / maxValue).toFloat().coerceIn(0f, 1f)
                val cRatio = (cost[i].value / maxValue).toFloat().coerceIn(0f, 1f)
                val rHeight = size.height * rRatio
                val cHeight = size.height * cRatio
                val alphaForOthers = if (selected == null || selected == i) 1f else 0.55f

                drawRoundRect(
                    color = revenueColor.copy(alpha = alphaForOthers),
                    topLeft = Offset(baseX, size.height - rHeight),
                    size = Size(barWidthPx, rHeight),
                    cornerRadius = radius,
                )
                drawRoundRect(
                    color = costColor.copy(alpha = alphaForOthers),
                    topLeft = Offset(baseX + barWidthPx + barGapPx, size.height - cHeight),
                    size = Size(barWidthPx, cHeight),
                    cornerRadius = radius,
                )
            }
        }

        selected?.let { idx ->
            val r = revenue[idx]
            val c = cost[idx]
            val tallerRatio = maxOf(
                (r.value / maxValue).toFloat().coerceIn(0f, 1f),
                (c.value / maxValue).toFloat().coerceIn(0f, 1f),
            )
            val barTopPx = heightPx - heightPx * tallerRatio
            val groupCenterPx = idx * (groupWidthPx + pairGapPx) + groupWidthPx / 2f
            PharmGroupedBarTooltip(
                label = r.label,
                revenueLabel = revenueLegend,
                revenueValue = valueFormatter(r.value),
                revenueColor = revenueColor,
                costLabel = costLegend,
                costValue = valueFormatter(c.value),
                costColor = costColor,
                anchorXPx = groupCenterPx,
                anchorYPx = barTopPx,
                hostWidthPx = widthPx,
            )
        }
    }
}

@Composable
private fun PharmBarTooltip(
    label: String,
    value: String,
    anchorXPx: Float,
    anchorYPx: Float,
    hostWidthPx: Float,
) {
    val t = pharmTokens
    val density = LocalDensity.current
    val estimatedTooltipWidthPx = with(density) { 120.dp.toPx() }
    val tooltipHeightDp = 44.dp
    val tooltipHeightPx = with(density) { tooltipHeightDp.toPx() }
    val gapAboveBarPx = with(density) { 6.dp.toPx() }

    val rawLeftPx = anchorXPx - estimatedTooltipWidthPx / 2f
    val clampedLeftPx = rawLeftPx.coerceIn(0f, (hostWidthPx - estimatedTooltipWidthPx).coerceAtLeast(0f))
    val topPx = (anchorYPx - tooltipHeightPx - gapAboveBarPx).coerceAtLeast(0f)

    Box(
        modifier = Modifier
            .padding(start = with(density) { clampedLeftPx.toDp() }, top = with(density) { topPx.toDp() })
            .width(with(density) { estimatedTooltipWidthPx.toDp() })
            .height(tooltipHeightDp)
            .clip(RoundedCornerShape(8.dp))
            .background(t.colors.fg1)
            .border(1.dp, t.colors.fg1, RoundedCornerShape(8.dp))
            .padding(horizontal = 10.dp, vertical = 6.dp),
    ) {
        androidx.compose.foundation.layout.Column {
            Text(
                text = label,
                style = PharmText.micro.copy(color = t.colors.surface.copy(alpha = 0.72f)),
            )
            Text(
                text = value,
                style = PharmText.body.copy(color = t.colors.surface, fontWeight = FontWeight.SemiBold),
            )
        }
    }
}

@Composable
private fun PharmGroupedBarTooltip(
    label: String,
    revenueLabel: String,
    revenueValue: String,
    revenueColor: Color,
    costLabel: String,
    costValue: String,
    costColor: Color,
    anchorXPx: Float,
    anchorYPx: Float,
    hostWidthPx: Float,
) {
    val t = pharmTokens
    val density = LocalDensity.current
    val estimatedTooltipWidthPx = with(density) { 168.dp.toPx() }
    val tooltipHeightDp = 72.dp
    val tooltipHeightPx = with(density) { tooltipHeightDp.toPx() }
    val gapAboveBarPx = with(density) { 6.dp.toPx() }

    val rawLeftPx = anchorXPx - estimatedTooltipWidthPx / 2f
    val clampedLeftPx = rawLeftPx.coerceIn(0f, (hostWidthPx - estimatedTooltipWidthPx).coerceAtLeast(0f))
    val topPx = (anchorYPx - tooltipHeightPx - gapAboveBarPx).coerceAtLeast(0f)

    Box(
        modifier = Modifier
            .padding(start = with(density) { clampedLeftPx.toDp() }, top = with(density) { topPx.toDp() })
            .width(with(density) { estimatedTooltipWidthPx.toDp() })
            .height(tooltipHeightDp)
            .clip(RoundedCornerShape(8.dp))
            .background(t.colors.fg1)
            .padding(horizontal = 10.dp, vertical = 6.dp),
    ) {
        androidx.compose.foundation.layout.Column(
            verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(2.dp),
        ) {
            Text(
                text = label,
                style = PharmText.micro.copy(color = t.colors.surface.copy(alpha = 0.72f)),
            )
            TooltipRow(color = revenueColor, label = revenueLabel, value = revenueValue)
            TooltipRow(color = costColor, label = costLabel, value = costValue)
        }
    }
}

@Composable
private fun TooltipRow(color: Color, label: String, value: String) {
    val t = pharmTokens
    Row(
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
        horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(6.dp),
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(color),
        )
        Text(
            text = label,
            style = PharmText.micro.copy(color = t.colors.surface.copy(alpha = 0.72f)),
            modifier = Modifier.weight(1f),
        )
        Text(
            text = value,
            style = PharmText.micro.copy(color = t.colors.surface, fontWeight = FontWeight.SemiBold),
        )
    }
}
