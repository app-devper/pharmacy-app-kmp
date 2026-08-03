package app.devper.pharm.ui.designsystem

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.tween
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import app.devper.pharm.ui.common.pharmFocusRing
import app.devper.pharm.ui.components.LocalWindowSize
import app.devper.pharm.ui.i18n.pharmStrings
import app.devper.pharm.ui.theme.PharmText
import app.devper.pharm.ui.theme.pharmTokens
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.runtime.CompositionLocalProvider

enum class PharmButtonVariant { Primary, Secondary, Danger, Ghost, Outline }
enum class PharmButtonSize { Sm, Md, Lg }

@Composable
fun PharmButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: PharmButtonVariant = PharmButtonVariant.Primary,
    size: PharmButtonSize = PharmButtonSize.Md,
    enabled: Boolean = true,
    loading: Boolean = false,
    leadingIcon: (@Composable () -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    val t = pharmTokens
    val compactTopbarAction = LocalCompactTopbarActions.current
    val resolvedLeadingIcon = leadingIcon.takeUnless { compactTopbarAction }
    val (bg, fg, border) = colorsFor(variant)
    val padding = paddingFor(
        size = size,
        smallHorizontalPadding = if (compactTopbarAction) {
            t.dimens.compactTopbarActionPaddingX
        } else {
            t.dimens.buttonSmPaddingX
        },
    )
    val baseMinHeight = when (size) {
        PharmButtonSize.Sm -> t.dimens.buttonSmHeight
        PharmButtonSize.Md -> t.dimens.buttonMdHeight
        PharmButtonSize.Lg -> t.dimens.controlHeight
    }
    val minHeight = responsiveButtonMinHeight(
        baseMinHeight = baseMinHeight,
        compact = LocalWindowSize.current.isCompact,
        compactTopbarAction = compactTopbarAction,
        minimumTouchTarget = t.dimens.minimumTouchTarget,
    )
    val shape = t.shapes.pill
    val interactive = enabled && !loading
    val interaction = remember { MutableInteractionSource() }
    val hovered by interaction.collectIsHoveredAsState()
    val reducedMotion = LocalReducedMotion.current
    val resolvedBg by animateColorAsState(
        targetValue = if (interactive && hovered) hoverBackgroundFor(variant) else bg,
        animationSpec = if (reducedMotion) snap() else tween(PharmMotion.Fast),
        label = "pharmButtonBackground",
    )
    val indication = LocalIndication.current
    val loadingDescription = pharmStrings.commonLoading

    Row(
        modifier = modifier
            .then(
                if (compactTopbarAction) {
                    Modifier.widthIn(max = t.dimens.compactTopbarActionMaxWidth)
                } else {
                    Modifier
                },
            )
            .heightIn(min = minHeight)
            .pharmFocusRing(interactionSource = interaction, shape = shape)
            .clip(shape)
            .alpha(if (enabled || loading) 1f else 0.5f)
            .then(if (border != null) Modifier.border(1.dp, border, shape) else Modifier)
            .background(resolvedBg, shape)
            .semantics(mergeDescendants = true) {
                role = Role.Button
                if (loading) stateDescription = loadingDescription
            }
            .clickable(
                enabled = interactive,
                onClick = onClick,
                interactionSource = interaction,
                indication = indication,
            )
            .padding(padding),
        horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CompositionLocalProvider(LocalContentColor provides fg) {
            if (loading) {
                CircularProgressIndicator(
                    color = fg,
                    strokeWidth = 2.dp,
                    modifier = Modifier.size(14.dp),
                )
            } else if (resolvedLeadingIcon != null) {
                resolvedLeadingIcon()
            }
            content()
        }
    }
}

internal fun responsiveButtonMinHeight(
    baseMinHeight: Dp,
    compact: Boolean,
    compactTopbarAction: Boolean,
    minimumTouchTarget: Dp,
): Dp = if (compact || compactTopbarAction) {
    baseMinHeight.coerceAtLeast(minimumTouchTarget)
} else {
    baseMinHeight
}

@Composable
fun PharmButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: PharmButtonVariant = PharmButtonVariant.Primary,
    size: PharmButtonSize = PharmButtonSize.Md,
    enabled: Boolean = true,
    loading: Boolean = false,
    leadingIcon: (@Composable () -> Unit)? = null,
) {
    val iconOnly = LocalCompactIconOnlyActions.current && leadingIcon != null
    PharmButton(
        onClick = onClick,
        modifier = if (iconOnly) modifier.semantics { contentDescription = label } else modifier,
        variant = variant,
        size = size,
        enabled = enabled,
        loading = loading,
        leadingIcon = leadingIcon,
    ) {
        if (!iconOnly) {
            val style = if (size == PharmButtonSize.Sm) PharmText.buttonSm else PharmText.buttonMd
            Text(
                text = label,
                style = style,
                textAlign = TextAlign.Center,
                maxLines = 1,
                softWrap = false,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun colorsFor(variant: PharmButtonVariant): Triple<Color, Color, Color?> {
    val c = pharmTokens.colors
    return when (variant) {
        PharmButtonVariant.Primary   -> Triple(c.primaryActionBg,    c.primaryActionFg,   null)
        PharmButtonVariant.Secondary -> Triple(c.secondaryActionBg,  c.secondaryActionFg, null)
        PharmButtonVariant.Danger    -> Triple(c.dangerActionBg,     c.dangerActionFg,    null)
        PharmButtonVariant.Ghost     -> Triple(Color.Transparent,    c.fg2,      null)
        PharmButtonVariant.Outline   -> Triple(c.surface,            c.fg2,      c.border)
    }
}

@Composable
private fun hoverBackgroundFor(variant: PharmButtonVariant): Color {
    val c = pharmTokens.colors
    return when (variant) {
        PharmButtonVariant.Primary -> c.primaryActionBg.copy(alpha = 0.82f)
        PharmButtonVariant.Secondary -> c.secondaryActionBgHover
        PharmButtonVariant.Danger -> c.dangerActionBg.copy(alpha = 0.82f)
        PharmButtonVariant.Ghost, PharmButtonVariant.Outline -> c.hoverSurface
    }
}

private fun paddingFor(
    size: PharmButtonSize,
    smallHorizontalPadding: Dp,
): PaddingValues = when (size) {
    PharmButtonSize.Sm -> PaddingValues(horizontal = smallHorizontalPadding, vertical = 6.dp)
    PharmButtonSize.Md -> PaddingValues(horizontal = 16.dp, vertical = 8.dp)
    PharmButtonSize.Lg -> PaddingValues(horizontal = 20.dp, vertical = 10.dp)
}
