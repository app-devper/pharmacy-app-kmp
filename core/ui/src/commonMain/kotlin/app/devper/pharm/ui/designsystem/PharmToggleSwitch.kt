package app.devper.pharm.ui.designsystem

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.toggleable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import app.devper.pharm.ui.common.pharmFocusRing
import app.devper.pharm.ui.theme.pharmTokens

@Composable
fun PharmToggleSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val t = pharmTokens
    val trackBg = if (checked) t.colors.accent else t.colors.border
    val knobAlign = if (checked) Alignment.CenterEnd else Alignment.CenterStart
    val interaction = remember { MutableInteractionSource() }
    val indication = LocalIndication.current

    Box(
        modifier = modifier
            .sizeIn(minWidth = 48.dp, minHeight = 48.dp)
            .toggleable(
                value = checked,
                interactionSource = interaction,
                indication = indication,
                enabled = enabled,
                role = Role.Switch,
                onValueChange = onCheckedChange,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .alpha(if (enabled) 1f else 0.5f)
                .pharmFocusRing(interactionSource = interaction, shape = t.shapes.pill)
                .padding(2.dp)
                .width(36.dp)
                .height(20.dp)
                .clip(t.shapes.pill)
                .background(trackBg)
                .padding(2.dp),
            contentAlignment = knobAlign,
        ) {
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .clip(t.shapes.pill)
                    .background(t.colors.surface),
            )
        }
    }
}
