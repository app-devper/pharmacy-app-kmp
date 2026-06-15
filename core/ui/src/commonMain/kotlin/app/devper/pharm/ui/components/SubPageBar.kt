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
class SubPageBarContent(
    val token: Any,
    val title: String,
    val onBack: () -> Unit,
    val actions: (@Composable () -> Unit)?,
)

@Stable
class SubPageBarController {
    var content: SubPageBarContent? by mutableStateOf(null)
}

val LocalSubPageBarController = staticCompositionLocalOf<SubPageBarController?> { null }

@Composable
fun SubPageBar(
    title: String,
    onBack: () -> Unit,
    actions: (@Composable () -> Unit)? = null,
) {
    val controller = LocalSubPageBarController.current ?: return
    val token = remember { Any() }
    SideEffect {
        controller.content = SubPageBarContent(token, title, onBack, actions)
    }
    DisposableEffect(Unit) {
        onDispose {
            if (controller.content?.token === token) {
                controller.content = null
            }
        }
    }
}
