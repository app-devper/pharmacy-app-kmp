package app.devper.pharm.ui.designsystem

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import app.devper.pharm.ui.theme.PharmText
import app.devper.pharm.ui.theme.pharmTokens
import app.devper.pharm.ui.common.pharmClickable

enum class MetricTint { Neutral, Success, Warning, Danger }

private val METRIC_CARD_MIN_WIDTH = 150.dp
private val METRIC_CARD_GAP = 8.dp

@Composable
fun MetricCardRow(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val scrollState = rememberScrollState()
    BoxWithConstraints(modifier = modifier.fillMaxWidth()) {
        val viewportPx = with(LocalDensity.current) { maxWidth.roundToPx() }
        Layout(
            content = content,
            modifier = Modifier.horizontalScroll(scrollState),
        ) { measurables, _ ->
            val count = measurables.size
            if (count == 0) return@Layout layout(0, 0) {}
            val gapPx = METRIC_CARD_GAP.roundToPx()
            val minWidthPx = METRIC_CARD_MIN_WIDTH.roundToPx()
            val totalGap = gapPx * (count - 1)
            val fillWidth = (viewportPx - totalGap) / count
            val cardWidth = maxOf(minWidthPx, fillWidth)

            val tallest = measurables.maxOf { it.maxIntrinsicHeight(cardWidth) }
            val placeables = measurables.map {
                it.measure(Constraints.fixed(cardWidth, tallest))
            }
            val rowWidth = cardWidth * count + totalGap
            layout(rowWidth, tallest) {
                var x = 0
                placeables.forEach { placeable ->
                    placeable.placeRelative(x, 0)
                    x += cardWidth + gapPx
                }
            }
        }
        if (scrollState.canScrollForward) {
            val fadeTo = pharmTokens.colors.bgPage
            val fadeBrush = remember(fadeTo) {
                Brush.horizontalGradient(listOf(Color.Transparent, fadeTo))
            }
            Box(modifier = Modifier.matchParentSize(), contentAlignment = Alignment.CenterEnd) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(24.dp)
                        .background(fadeBrush),
                )
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
    val t = pharmTokens
    val valueColor: Color = when (tint) {
        MetricTint.Neutral -> t.colors.fg1
        MetricTint.Success -> t.colors.successFg
        MetricTint.Warning -> t.colors.warningFg
        MetricTint.Danger  -> t.colors.dangerFg
    }
    val bg = when (tint) {
        MetricTint.Neutral -> t.colors.surface
        MetricTint.Success -> t.colors.successBg.copy(alpha = 0.25f)
        MetricTint.Warning -> t.colors.warningBg.copy(alpha = 0.35f)
        MetricTint.Danger  -> t.colors.dangerBg.copy(alpha = 0.35f)
    }
    val borderColor = when (tint) {
        MetricTint.Neutral -> t.colors.borderSubtle
        MetricTint.Success -> t.colors.successFg.copy(alpha = 0.2f)
        MetricTint.Warning -> t.colors.warningFg.copy(alpha = 0.2f)
        MetricTint.Danger  -> t.colors.dangerFg.copy(alpha = 0.2f)
    }
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
            .background(bg, shape)
            .border(1.dp, borderColor, shape)
            .then(clickMod)
            .padding(horizontal = t.spacing.s4, vertical = t.spacing.s3),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(text = label, style = PharmText.meta)
        Text(text = value, style = PharmText.metric.copy(color = valueColor))
        if (sub != null) {
            Text(text = sub, style = PharmText.micro.copy(color = t.colors.fgMuted))
        }
    }
}
