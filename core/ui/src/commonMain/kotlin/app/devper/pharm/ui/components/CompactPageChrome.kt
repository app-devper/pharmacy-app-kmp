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
sealed interface CompactPageChrome {
    val actions: (@Composable () -> Unit)?

    class Header(
        val title: String,
        val onBack: () -> Unit,
        override val actions: (@Composable () -> Unit)?,
    ) : CompactPageChrome

    class Actions(override val actions: @Composable () -> Unit) : CompactPageChrome
}

@Stable
class CompactPageChromeController {
    var content: CompactPageChrome? by mutableStateOf(null)
        private set

    private data class Registration(val sequence: Long, val chrome: CompactPageChrome)

    private val registrations = mutableMapOf<Any, Registration>()
    private var nextSequence = 0L

    fun register(token: Any, chrome: CompactPageChrome) {
        val sequence = registrations[token]?.sequence ?: nextSequence++
        registrations[token] = Registration(sequence, chrome)
        updateContent()
    }

    fun unregister(token: Any) {
        if (registrations.remove(token) != null) updateContent()
    }

    private fun updateContent() {
        content = registrations.values
            .filter { it.chrome is CompactPageChrome.Header }
            .maxByOrNull { it.sequence }
            ?.chrome
            ?: registrations.values.maxByOrNull { it.sequence }?.chrome
    }
}

val LocalCompactPageChromeController = staticCompositionLocalOf<CompactPageChromeController?> { null }

@Composable
fun RegisterCompactPageChrome(chrome: CompactPageChrome) {
    val controller = LocalCompactPageChromeController.current ?: return
    val token = remember { Any() }
    SideEffect { controller.register(token, chrome) }
    DisposableEffect(controller, token) {
        onDispose { controller.unregister(token) }
    }
}
