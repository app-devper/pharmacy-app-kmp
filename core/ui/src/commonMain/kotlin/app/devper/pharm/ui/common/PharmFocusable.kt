package app.devper.pharm.ui.common

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import app.devper.pharm.ui.theme.pharmTokens

@Composable
fun Modifier.pharmFocusable(
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    shape: Shape = pharmTokens.shapes.md,
    ringWidth: Dp = 2.dp,
): Modifier = this
    .pharmFocusRing(interactionSource = interactionSource, shape = shape, ringWidth = ringWidth)
    .focusable(enabled = enabled, interactionSource = interactionSource)

@Composable
fun Modifier.pharmFocusRing(
    interactionSource: MutableInteractionSource,
    shape: Shape = pharmTokens.shapes.md,
    ringWidth: Dp = 2.dp,
): Modifier {
    val isFocused by interactionSource.collectIsFocusedAsState()
    val ringColor by animateColorAsState(
        targetValue = if (isFocused) pharmTokens.colors.focusRing else Color.Transparent,
        animationSpec = tween(durationMillis = 120),
        label = "pharmFocusRing",
    )
    return this.border(width = ringWidth, color = ringColor, shape = shape)
}
