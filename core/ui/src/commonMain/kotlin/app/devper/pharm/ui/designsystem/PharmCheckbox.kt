package app.devper.pharm.ui.designsystem

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.selection.triStateToggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.unit.dp
import androidx.compose.ui.state.ToggleableState
import app.devper.pharm.ui.theme.pharmTokens

@Composable
fun PharmCheckbox(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    contentDescription: String? = null,
) {
    val toggleModifier = if (onCheckedChange != null) {
        Modifier.toggleable(
            value = checked,
            enabled = enabled,
            role = Role.Checkbox,
            onValueChange = onCheckedChange,
        )
    } else {
        Modifier
    }
    PharmCheckboxBox(
        state = if (checked) ToggleableState.On else ToggleableState.Off,
        enabled = enabled,
        contentDescription = contentDescription,
        modifier = modifier.then(toggleModifier),
    )
}

@Composable
fun PharmTriStateCheckbox(
    state: ToggleableState,
    onClick: (() -> Unit)?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    contentDescription: String? = null,
) {
    val toggleModifier = if (onClick != null) {
        Modifier.triStateToggleable(
            state = state,
            enabled = enabled,
            role = Role.Checkbox,
            onClick = onClick,
        )
    } else {
        Modifier
    }
    PharmCheckboxBox(
        state = state,
        enabled = enabled,
        contentDescription = contentDescription,
        modifier = modifier.then(toggleModifier),
    )
}

@Composable
private fun PharmCheckboxBox(
    state: ToggleableState,
    enabled: Boolean,
    contentDescription: String?,
    modifier: Modifier = Modifier,
) {
    val t = pharmTokens
    val shape = RoundedCornerShape(4.dp)
    val active = state != ToggleableState.Off
    val borderColor = when {
        !enabled -> t.colors.borderSubtle
        active -> t.colors.accent
        else -> t.colors.border
    }
    val fill = when {
        !enabled && active -> t.colors.borderSubtle
        active -> t.colors.accent
        else -> Color.Transparent
    }
    val semanticsModifier = if (contentDescription != null) {
        Modifier.clearAndSetSemantics { this.contentDescription = contentDescription }
    } else {
        Modifier
    }
    Box(
        modifier = modifier
            .then(semanticsModifier)
            .size(18.dp)
            .clip(shape)
            .background(fill, shape)
            .border(1.dp, borderColor, shape),
        contentAlignment = Alignment.Center,
    ) {
        when (state) {
            ToggleableState.On -> Icon(
                imageVector = PharmIcons.Check,
                contentDescription = null,
                tint = t.colors.surface,
                modifier = Modifier.size(12.dp),
            )
            ToggleableState.Indeterminate -> Box(
                modifier = Modifier
                    .size(width = 9.dp, height = 2.dp)
                    .clip(RoundedCornerShape(1.dp))
                    .background(t.colors.surface),
            )
            ToggleableState.Off -> Unit
        }
    }
}
