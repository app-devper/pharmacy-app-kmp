package app.devper.pharm.ui.designsystem

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import app.devper.pharm.ui.theme.pharmTokens
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun PharmBrandMark(
    modifier: Modifier = Modifier,
    size: Dp = 32.dp,
) {
    val colors = pharmTokens.colors
    Canvas(modifier = modifier.size(size)) {
        val s = this.size.minDimension
        drawRoundRect(
            brush = Brush.linearGradient(listOf(colors.accent, colors.accentHover)),
            cornerRadius = CornerRadius(s * 0.24f, s * 0.24f),
        )
        val arm = s * 0.50f
        val thick = s * 0.135f
        val cx = s / 2f
        val cy = s / 2f
        val rc = CornerRadius(thick * 0.4f, thick * 0.4f)
        drawRoundRect(
            color = Color.White,
            topLeft = Offset(cx - thick / 2f, cy - arm / 2f),
            size = Size(thick, arm),
            cornerRadius = rc,
        )
        drawRoundRect(
            color = Color.White,
            topLeft = Offset(cx - arm / 2f, cy - thick / 2f),
            size = Size(arm, thick),
            cornerRadius = rc,
        )
    }
}

@Preview
@Composable
private fun PharmBrandMark_Preview() {
    PharmLightPreview {
        PharmBrandMark(modifier = Modifier.size(48.dp))
    }
}
