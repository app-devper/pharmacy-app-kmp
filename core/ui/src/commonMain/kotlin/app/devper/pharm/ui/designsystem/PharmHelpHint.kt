package app.devper.pharm.ui.designsystem

import app.devper.pharm.ui.i18n.pharmStrings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import app.devper.pharm.ui.theme.PharmText
import app.devper.pharm.ui.theme.pharmTokens
import app.devper.pharm.ui.common.pharmClickable

@Composable
fun PharmHelpHint(
    text: String,
    modifier: Modifier = Modifier,
    label: String = pharmStrings.commonHelp,
) {
    val t = pharmTokens
    var open by remember { mutableStateOf(false) }
    val offsetY = with(LocalDensity.current) { 22.dp.roundToPx() }

    Box(modifier = modifier) {
        Box(
            modifier = Modifier
                .defaultMinSize(minWidth = 48.dp, minHeight = 48.dp)
                .clip(t.shapes.pill)
                .pharmClickable(role = Role.Button, shape = t.shapes.pill) { open = !open },
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = PharmIcons.Info,
                contentDescription = label,
                tint = t.colors.fgMuted,
                modifier = Modifier.size(16.dp),
            )
        }
        if (open) {
            Popup(
                alignment = Alignment.TopStart,
                offset = IntOffset(0, offsetY),
                onDismissRequest = { open = false },
                properties = PopupProperties(focusable = true),
            ) {
                Box(
                    modifier = Modifier
                        .widthIn(max = 260.dp)
                        .clip(t.shapes.md)
                        .background(t.colors.surfaceRaised, t.shapes.md)
                        .border(1.dp, t.colors.border, t.shapes.md)
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                ) {
                    Text(text = text, style = PharmText.micro.copy(color = t.colors.fg2))
                }
            }
        }
    }
}
