package app.devper.pharm.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf

@Immutable
class CompactPageHeaderContent(
    val token: Any,
    val title: String,
    val onBack: () -> Unit,
    val actions: (@Composable () -> Unit)?,
)

@Stable
class CompactPageHeaderController {
    var content: CompactPageHeaderContent? by mutableStateOf(null)
}

val LocalCompactPageHeaderController = staticCompositionLocalOf<CompactPageHeaderController?> { null }

@Composable
fun CompactPageHeader(
    title: String,
    onBack: () -> Unit,
    actions: (@Composable () -> Unit)?,
) {
    val controller = LocalCompactPageHeaderController.current ?: return
    val token = remember { Any() }
    SideEffect {
        controller.content = CompactPageHeaderContent(token, title, onBack, actions)
    }
    DisposableEffect(controller, token) {
        onDispose {
            if (controller.content?.token === token) controller.content = null
        }
    }
}
