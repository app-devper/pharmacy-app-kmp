package app.devper.pharm.ui.designsystem

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
    val shape = t.shapes.lg
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(t.colors.surface, shape)
            .border(1.dp, t.colors.borderSubtle, shape)
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
