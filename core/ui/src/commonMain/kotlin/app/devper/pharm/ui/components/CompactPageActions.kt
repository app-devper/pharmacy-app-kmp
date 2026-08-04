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
class CompactPageActionsContent(
    val token: Any,
    val actions: @Composable () -> Unit,
)

@Stable
class CompactPageActionsController {
    var content: CompactPageActionsContent? by mutableStateOf(null)
}

val LocalCompactPageActionsController = staticCompositionLocalOf<CompactPageActionsController?> { null }

@Composable
fun CompactPageActions(actions: @Composable () -> Unit) {
    val controller = LocalCompactPageActionsController.current ?: return
    val token = remember { Any() }
    SideEffect {
        controller.content = CompactPageActionsContent(token, actions)
    }
    DisposableEffect(Unit) {
        onDispose {
            if (controller.content?.token === token) {
                controller.content = null
            }
        }
    }
}
