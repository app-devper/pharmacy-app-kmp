package app.devper.pharm.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.isAltPressed
import androidx.compose.ui.input.key.isCtrlPressed
import androidx.compose.ui.input.key.isMetaPressed
import androidx.compose.ui.input.key.isShiftPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import app.devper.pharm.ui.theme.PharmText
import app.devper.pharm.ui.theme.pharmTokens

@Immutable
data class PharmShortcut(
    val key: Key,
    val ctrl: Boolean = false,
    val shift: Boolean = false,
    val alt: Boolean = false,
    val label: String,
    val action: () -> Unit,
)

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun Modifier.pharmShortcuts(vararg shortcuts: PharmShortcut): Modifier = composed {
    val current = rememberUpdatedState(shortcuts)
    this.onPreviewKeyEvent { event ->
        if (event.type != KeyEventType.KeyDown) return@onPreviewKeyEvent false
        val matched = current.value.firstOrNull { matches(event, it) }
        if (matched != null) {
            matched.action()
            true
        } else {
            false
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
private fun matches(event: KeyEvent, shortcut: PharmShortcut): Boolean {
    if (event.key != shortcut.key) return false
    val ctrlOrCmd = event.isCtrlPressed || event.isMetaPressed
    if (ctrlOrCmd != shortcut.ctrl) return false
    if (event.isShiftPressed != shortcut.shift) return false
    if (event.isAltPressed != shortcut.alt) return false
    return true
}

@Composable
fun ShortcutHint(label: String, modifier: Modifier = Modifier) {
    val t = pharmTokens
    Text(
        text = label,
        style = PharmText.badgeSm.copy(
            color = t.colors.fg3,
            fontFamily = FontFamily.Monospace,
        ),
        modifier = modifier
            .clip(t.shapes.sm)
            .background(t.colors.borderSubtle)
            .border(1.dp, t.colors.border, t.shapes.sm)
            .padding(horizontal = 6.dp, vertical = 2.dp),
    )
}
