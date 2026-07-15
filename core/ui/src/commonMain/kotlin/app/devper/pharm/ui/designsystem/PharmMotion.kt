package app.devper.pharm.ui.designsystem

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import app.devper.pharm.ui.theme.PharmText
import app.devper.pharm.ui.theme.fmtBaht

object PharmMotion {
    const val Fast = 150
    const val Medium = 250
}

val LocalReducedMotion = staticCompositionLocalOf { false }

internal fun motionDurationMillis(reducedMotion: Boolean, durationMillis: Int): Int =
    if (reducedMotion) 0 else durationMillis

@Composable
fun pharmBannerEnter(): EnterTransition =
    if (LocalReducedMotion.current) EnterTransition.None
    else fadeIn(tween(PharmMotion.Fast)) + expandVertically(tween(PharmMotion.Medium))

@Composable
fun PharmAnimatedBaht(
    value: Double,
    modifier: Modifier = Modifier,
    style: TextStyle = PharmText.total,
) {
    val reducedMotion = LocalReducedMotion.current
    val animated by animateFloatAsState(
        targetValue = value.toFloat(),
        animationSpec = if (reducedMotion) snap() else tween(PharmMotion.Medium),
    )
    Text(text = fmtBaht(animated.toDouble()), style = style, modifier = modifier)
}
