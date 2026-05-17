package app.devper.pharm.ui.scanner

import androidx.compose.foundation.focusable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.key.utf16CodePoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.CoroutineScope
import androidx.compose.runtime.rememberCoroutineScope

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun Modifier.scanBarcodes(
    enabled: Boolean = true,
    minLength: Int = 3,
    idleTimeoutMillis: Long = 120,
    onScan: (String) -> Unit,
): Modifier = composed {
    if (!enabled) return@composed this

    val onScanState = rememberUpdatedState(onScan)
    val focusRequester = remember { FocusRequester() }
    val buffer = remember { StringBuilder() }
    val scope = rememberCoroutineScope()
    val flushJob = remember { Box<Job?>(null) }
    val flush = remember<(String) -> Unit>(scope) {
        { reason ->
            val payload = buffer.toString()
            buffer.clear()
            flushJob.value?.cancel()
            flushJob.value = null
            if (payload.length >= minLength) {
                onScanState.value(payload)
            }

            @Suppress("UNUSED_EXPRESSION") reason
        }
    }

    LaunchedEffect(Unit) {
        runCatching { focusRequester.requestFocus() }
    }

    this
        .focusRequester(focusRequester)
        .focusable()
        .onKeyEvent { event -> handleKeyEvent(event, buffer, scope, flushJob, flush, idleTimeoutMillis) }
}

private class Box<T>(var value: T)

@OptIn(ExperimentalComposeUiApi::class)
private fun handleKeyEvent(
    event: KeyEvent,
    buffer: StringBuilder,
    scope: CoroutineScope,
    flushJob: Box<Job?>,
    flush: (String) -> Unit,
    idleTimeoutMillis: Long,
): Boolean {
    if (event.type != KeyEventType.KeyDown) return false

    if (event.key == androidx.compose.ui.input.key.Key.Enter ||
        event.key == androidx.compose.ui.input.key.Key.NumPadEnter
    ) {
        if (buffer.isNotEmpty()) {
            flush("enter")
            return true
        }
        return false
    }

    val cp = event.utf16CodePoint

    if (cp in 0x20..0x7E) {
        buffer.append(cp.toChar())

        flushJob.value?.cancel()
        flushJob.value = scope.launch {
            delay(idleTimeoutMillis)
            if (buffer.isNotEmpty()) flush("timeout")
        }

        return false
    }

    return false
}
