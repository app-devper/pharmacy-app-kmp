package app.devper.pharm.ui.designsystem

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.FlowRowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import app.devper.pharm.ui.theme.PharmText
import app.devper.pharm.ui.theme.pharmTokens

enum class MetricTint { Blue, Indigo, Green, Purple }

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MetricCardRow(
    modifier: Modifier = Modifier,
    content: @Composable FlowRowScope.() -> Unit,
) {
    BoxWithConstraints(modifier = modifier.fillMaxWidth()) {
        val columns = when {
            maxWidth < 360.dp -> 1
            maxWidth < 720.dp -> 2
            else -> 4
        }
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            maxItemsInEachRow = columns,
            content = content,
        )
    }
}

@Composable
fun MetricCard(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    sub: String? = null,
    tint: MetricTint = MetricTint.Blue,
) {
    val t = pharmTokens
    val valueColor: Color = when (tint) {
        MetricTint.Blue   -> t.colors.accent
        MetricTint.Indigo -> t.colors.indigoFg
        MetricTint.Green  -> t.colors.successFg
        MetricTint.Purple -> t.colors.typePurpleFg
    }
    val bg = when (tint) {
        MetricTint.Blue   -> t.colors.infoBg.copy(alpha = 0.25f)
        MetricTint.Indigo -> t.colors.indigoBg.copy(alpha = 0.25f)
        MetricTint.Green  -> t.colors.successBg.copy(alpha = 0.25f)
        MetricTint.Purple -> t.colors.typePurpleBg.copy(alpha = 0.25f)
    }
    val borderColor = when (tint) {
        MetricTint.Blue   -> t.colors.infoFg.copy(alpha = 0.15f)
        MetricTint.Indigo -> t.colors.indigoFg.copy(alpha = 0.15f)
        MetricTint.Green  -> t.colors.successFg.copy(alpha = 0.15f)
        MetricTint.Purple -> t.colors.typePurpleFg.copy(alpha = 0.15f)
    }
    val shape = t.shapes.lg
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(bg, shape)
            .border(1.dp, borderColor, shape)
            .padding(t.spacing.s4),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(text = label, style = PharmText.meta)
        Text(text = value, style = PharmText.metric.copy(color = valueColor))
        if (sub != null) {
            Text(text = sub, style = PharmText.micro.copy(color = t.colors.fgMuted))
        }
    }
}
