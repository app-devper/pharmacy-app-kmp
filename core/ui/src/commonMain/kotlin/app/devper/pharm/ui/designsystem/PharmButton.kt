package app.devper.pharm.ui.designsystem

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import app.devper.pharm.ui.common.pharmFocusRing
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
    val (bg, fg, border) = colorsFor(variant)
    val padding = paddingFor(size)
    val shape = t.shapes.md
    val interactive = enabled && !loading
    val interaction = remember { MutableInteractionSource() }
    val indication = LocalIndication.current

    Row(
        modifier = modifier
            .heightIn(min = t.dimens.controlHeight)
            .pharmFocusRing(interactionSource = interaction, shape = shape)
            .clip(shape)
            .alpha(if (enabled || loading) 1f else 0.5f)
            .then(if (border != null) Modifier.border(1.dp, border, shape) else Modifier)
            .background(bg, shape)
            .semantics { role = Role.Button }
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
            } else if (leadingIcon != null) {
                leadingIcon()
            }
            content()
        }
    }
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
) = PharmButton(
    onClick = onClick,
    modifier = modifier,
    variant = variant,
    size = size,
    enabled = enabled,
    loading = loading,
    leadingIcon = leadingIcon,
) {
    val style = if (size == PharmButtonSize.Sm) PharmText.buttonSm else PharmText.buttonMd
    Text(
        text = label,
        style = style,
        textAlign = TextAlign.Center,
        maxLines = 1,
        softWrap = false,
    )
}

@Composable
private fun colorsFor(variant: PharmButtonVariant): Triple<Color, Color, Color?> {
    val c = pharmTokens.colors
    return when (variant) {
        PharmButtonVariant.Primary   -> Triple(c.accent,             c.surface,  null)
        PharmButtonVariant.Secondary -> Triple(c.borderSubtle,       c.fg2,      null)
        PharmButtonVariant.Danger    -> Triple(c.dangerFg,           c.surface,  null)
        PharmButtonVariant.Ghost     -> Triple(Color.Transparent,    c.fg2,      null)
        PharmButtonVariant.Outline   -> Triple(c.surface,            c.fg2,      c.border)
    }
}

private fun paddingFor(size: PharmButtonSize): PaddingValues = when (size) {
    PharmButtonSize.Sm -> PaddingValues(horizontal = 12.dp, vertical = 6.dp)
    PharmButtonSize.Md -> PaddingValues(horizontal = 16.dp, vertical = 8.dp)
    PharmButtonSize.Lg -> PaddingValues(horizontal = 20.dp, vertical = 10.dp)
}
