package app.devper.pharm.ui.designsystem

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import app.devper.pharm.ui.common.pharmClickable
import app.devper.pharm.ui.theme.pharmTokens

internal const val PHARM_ICON_HOVER_SCALE = 1.08f

internal fun iconHoverTargetScale(
    enabled: Boolean,
    hovered: Boolean,
    reducedMotion: Boolean,
): Float = if (enabled && hovered && !reducedMotion) PHARM_ICON_HOVER_SCALE else 1f

internal fun showsIconButtonContainer(
    enabled: Boolean,
    hovered: Boolean,
    selected: Boolean,
): Boolean = selected || (enabled && hovered)

@Composable
fun PharmIconButton(
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    selected: Boolean = false,
    minSize: Dp = 48.dp,
    shape: Shape = pharmTokens.shapes.pill,
    content: @Composable () -> Unit,
) {
    val t = pharmTokens
    val interactionSource = remember { MutableInteractionSource() }
    val hovered by interactionSource.collectIsHoveredAsState()
    val reducedMotion = LocalReducedMotion.current
    val contentScale by animateFloatAsState(
        targetValue = iconHoverTargetScale(
            enabled = enabled,
            hovered = hovered,
            reducedMotion = reducedMotion,
        ),
        animationSpec = tween(durationMillis = motionDurationMillis(reducedMotion, PharmMotion.Fast)),
        label = "pharmIconHoverScale",
    )
    val containerColor by animateColorAsState(
        targetValue = if (
            showsIconButtonContainer(
                enabled = enabled,
                hovered = hovered,
                selected = selected,
            )
        ) {
            t.colors.hoverSurface
        } else {
            Color.Transparent
        },
        animationSpec = tween(durationMillis = motionDurationMillis(reducedMotion, PharmMotion.Fast)),
        label = "pharmIconButtonContainer",
    )
    Box(
        modifier = modifier
            .sizeIn(minWidth = minSize, minHeight = minSize)
            .clip(shape)
            .background(containerColor, shape)
            .pharmClickable(
                enabled = enabled,
                role = Role.Button,
                shape = shape,
                interactionSource = interactionSource,
                onClick = onClick,
            )
            .semantics(mergeDescendants = true) {
                this.contentDescription = contentDescription
            },
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier.graphicsLayer {
                scaleX = contentScale
                scaleY = contentScale
            },
            contentAlignment = Alignment.Center,
        ) {
            content()
        }
    }
}
