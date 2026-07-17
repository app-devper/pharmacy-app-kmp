package app.devper.pharm.ui.common

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.border
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.toggleable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import app.devper.pharm.ui.designsystem.LocalReducedMotion
import app.devper.pharm.ui.designsystem.motionDurationMillis
import app.devper.pharm.ui.theme.pharmTokens

internal enum class FocusInteractionMode {
    Keyboard,
    Pointer,
}

internal data class FocusVisibilityState(
    val interactionMode: FocusInteractionMode = FocusInteractionMode.Keyboard,
) {
    fun onPointerPress(): FocusVisibilityState = copy(interactionMode = FocusInteractionMode.Pointer)

    fun onKeyPress(): FocusVisibilityState = copy(interactionMode = FocusInteractionMode.Keyboard)

    fun onFocusChanged(isFocused: Boolean): FocusVisibilityState =
        if (isFocused) this else FocusVisibilityState()

    fun isVisible(isFocused: Boolean): Boolean =
        isFocused && interactionMode == FocusInteractionMode.Keyboard
}

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
    var focusVisibility by remember(interactionSource) { mutableStateOf(FocusVisibilityState()) }
    LaunchedEffect(isFocused) {
        focusVisibility = focusVisibility.onFocusChanged(isFocused)
    }
    val reducedMotion = LocalReducedMotion.current
    val ringColor by animateColorAsState(
        targetValue = if (focusVisibility.isVisible(isFocused)) {
            pharmTokens.colors.focusRing
        } else {
            Color.Transparent
        },
        animationSpec = tween(durationMillis = motionDurationMillis(reducedMotion, 120)),
        label = "pharmFocusRing",
    )
    return this
        .pointerInput(interactionSource) {
            awaitEachGesture {
                awaitFirstDown(requireUnconsumed = false, pass = PointerEventPass.Initial)
                focusVisibility = focusVisibility.onPointerPress()
            }
        }
        .onPreviewKeyEvent { event ->
            if (event.type == KeyEventType.KeyDown) {
                focusVisibility = focusVisibility.onKeyPress()
            }
            false
        }
        .border(width = ringWidth, color = ringColor, shape = shape)
}

@Composable
fun Modifier.pharmClickable(
    enabled: Boolean = true,
    role: Role = Role.Button,
    shape: Shape = pharmTokens.shapes.md,
    onClick: () -> Unit,
): Modifier {
    val interactionSource = remember { MutableInteractionSource() }
    return this
        .pharmFocusRing(interactionSource = interactionSource, shape = shape)
        .clickable(
            interactionSource = interactionSource,
            indication = LocalIndication.current,
            enabled = enabled,
            role = role,
            onClick = onClick,
        )
}

@Composable
fun Modifier.pharmToggleable(
    value: Boolean,
    enabled: Boolean = true,
    role: Role = Role.Checkbox,
    shape: Shape = pharmTokens.shapes.md,
    onValueChange: (Boolean) -> Unit,
): Modifier {
    val interactionSource = remember { MutableInteractionSource() }
    return this
        .pharmFocusRing(interactionSource = interactionSource, shape = shape)
        .toggleable(
            value = value,
            interactionSource = interactionSource,
            indication = LocalIndication.current,
            enabled = enabled,
            role = role,
            onValueChange = onValueChange,
        )
}

@Composable
fun Modifier.pharmSelectable(
    selected: Boolean,
    enabled: Boolean = true,
    role: Role = Role.RadioButton,
    shape: Shape = pharmTokens.shapes.md,
    onClick: () -> Unit,
): Modifier {
    val interactionSource = remember { MutableInteractionSource() }
    return this
        .pharmFocusRing(interactionSource = interactionSource, shape = shape)
        .selectable(
            selected = selected,
            interactionSource = interactionSource,
            indication = LocalIndication.current,
            enabled = enabled,
            role = role,
            onClick = onClick,
        )
}
