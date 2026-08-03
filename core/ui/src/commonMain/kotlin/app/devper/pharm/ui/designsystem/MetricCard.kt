package app.devper.pharm.ui.designsystem

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import app.devper.pharm.ui.components.LocalWindowSize
import app.devper.pharm.ui.components.WindowSize
import app.devper.pharm.ui.theme.PharmText
import app.devper.pharm.ui.theme.pharmTokens
import app.devper.pharm.ui.common.pharmClickable

enum class MetricTint { Neutral, Success, Warning, Danger }

private val METRIC_CARD_GAP = 8.dp
private val METRIC_CARD_FOUR_COLUMN_WIDTH = 720.dp
private val METRIC_STAT_GAP = 6.dp

internal fun usesMetricStats(windowSize: WindowSize): Boolean = windowSize != WindowSize.Expanded

@Composable
@OptIn(ExperimentalLayoutApi::class)
fun MetricCardRow(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    if (usesMetricStats(LocalWindowSize.current)) {
        FlowRow(
            modifier = modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(METRIC_STAT_GAP),
            verticalArrangement = Arrangement.spacedBy(METRIC_STAT_GAP),
            itemVerticalAlignment = Alignment.CenterVertically,
        ) {
            content()
        }
        return
    }
    BoxWithConstraints(modifier = modifier.fillMaxWidth()) {
        val viewportPx = with(LocalDensity.current) { maxWidth.roundToPx() }
        Layout(
            content = content,
        ) { measurables, _ ->
            val count = measurables.size
            if (count == 0) return@Layout layout(0, 0) {}
            val columns = if (maxWidth >= METRIC_CARD_FOUR_COLUMN_WIDTH) minOf(4, count) else minOf(2, count)
            val rows = (count + columns - 1) / columns
            val gapPx = METRIC_CARD_GAP.roundToPx()
            val cardWidth = (viewportPx - gapPx * (columns - 1)) / columns

            val tallest = measurables.maxOf { it.maxIntrinsicHeight(cardWidth) }
            val placeables = measurables.map {
                it.measure(Constraints.fixed(cardWidth, tallest))
            }
            val rowHeight = tallest + gapPx
            val totalHeight = tallest * rows + gapPx * (rows - 1)
            layout(viewportPx, totalHeight) {
                placeables.forEachIndexed { index, placeable ->
                    val column = index % columns
                    val row = index / columns
                    placeable.placeRelative(
                        x = column * (cardWidth + gapPx),
                        y = row * rowHeight,
                    )
                }
            }
        }
    }
}

@Composable
fun MetricCard(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    sub: String? = null,
    tint: MetricTint = MetricTint.Neutral,
    onClick: (() -> Unit)? = null,
) {
    if (usesMetricStats(LocalWindowSize.current)) {
        MetricStat(
            label = label,
            value = value,
            modifier = modifier,
            tint = tint,
            onClick = onClick,
        )
        return
    }
    val t = pharmTokens
    val shape = t.shapes.lg
    val clickMod = if (onClick != null) {
        Modifier.pharmClickable(role = Role.Button, shape = shape, onClick = onClick)
    } else {
        Modifier
    }
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(metricSurface(tint), shape)
            .border(1.dp, metricBorder(tint), shape)
            .then(clickMod)
            .padding(horizontal = t.spacing.s4, vertical = t.spacing.s3),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(text = label, style = PharmText.meta)
        Text(text = value, style = PharmText.metric.copy(color = metricValueColor(tint)))
        if (sub != null) {
            Text(text = sub, style = PharmText.micro.copy(color = t.colors.fgMuted))
        }
    }
}

@Composable
private fun MetricStat(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    tint: MetricTint = MetricTint.Neutral,
    onClick: (() -> Unit)? = null,
) {
    val t = pharmTokens
    val shape = t.shapes.pill
    val clickMod = if (onClick != null) {
        Modifier.pharmClickable(role = Role.Button, shape = shape, onClick = onClick)
    } else {
        Modifier
    }
    Row(
        modifier = modifier
            .heightIn(min = pharmControlHeight)
            .clip(shape)
            .background(metricStatSurface(tint), shape)
            .border(1.dp, metricStatBorder(tint), shape)
            .then(clickMod)
            .padding(horizontal = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            style = PharmText.meta.copy(color = t.colors.fgMuted),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Text(
            text = value,
            style = PharmText.body.copy(
                color = metricValueColor(tint),
                fontWeight = FontWeight.SemiBold,
            ),
            maxLines = 1,
        )
    }
}

@Composable
private fun metricValueColor(tint: MetricTint): Color {
    val c = pharmTokens.colors
    return when (tint) {
        MetricTint.Neutral -> c.fg1
        MetricTint.Success -> c.successFg
        MetricTint.Warning -> c.warningFg
        MetricTint.Danger -> c.dangerFg
    }
}

@Composable
private fun metricSurface(tint: MetricTint): Color {
    val c = pharmTokens.colors
    return when (tint) {
        MetricTint.Neutral -> c.surface
        MetricTint.Success -> c.successBg.copy(alpha = 0.25f)
        MetricTint.Warning -> c.warningBg.copy(alpha = 0.35f)
        MetricTint.Danger -> c.dangerBg.copy(alpha = 0.35f)
    }
}

@Composable
private fun metricBorder(tint: MetricTint): Color {
    val c = pharmTokens.colors
    return when (tint) {
        MetricTint.Neutral -> c.borderSubtle
        MetricTint.Success -> c.successFg.copy(alpha = 0.2f)
        MetricTint.Warning -> c.warningFg.copy(alpha = 0.2f)
        MetricTint.Danger -> c.dangerFg.copy(alpha = 0.2f)
    }
}

@Composable
private fun metricStatSurface(tint: MetricTint): Color {
    val c = pharmTokens.colors
    return when (tint) {
        MetricTint.Neutral -> Color.Transparent
        MetricTint.Success -> c.successBg
        MetricTint.Warning -> c.warningBg
        MetricTint.Danger -> c.dangerBg
    }
}

@Composable
private fun metricStatBorder(tint: MetricTint): Color {
    val c = pharmTokens.colors
    return if (tint == MetricTint.Neutral) c.border else Color.Transparent
}
