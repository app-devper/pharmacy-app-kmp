package app.devper.pharm.ui.designsystem

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource

internal fun collapsedHeaderOffset(current: Float, delta: Float, maxCollapse: Float): Float =
    (current + delta).coerceIn(-maxCollapse, 0f)

internal fun headerScrollConsumption(current: Float, delta: Float, maxCollapse: Float): Float =
    collapsedHeaderOffset(current, delta, maxCollapse) - current

@Stable
class CollapsibleHeaderState {
    var headerHeightPx by mutableStateOf(0)
        internal set
    var offsetPx by mutableStateOf(0f)
        private set

    internal fun consume(delta: Float): Float {
        val consumed = headerScrollConsumption(offsetPx, delta, headerHeightPx.toFloat())
        offsetPx += consumed
        return consumed
    }

    internal fun onHeaderMeasured(heightPx: Int) {
        if (heightPx == headerHeightPx) return
        headerHeightPx = heightPx
        offsetPx = offsetPx.coerceAtLeast(-heightPx.toFloat())
    }
}

@Composable
fun rememberCollapsibleHeaderState(): CollapsibleHeaderState = remember { CollapsibleHeaderState() }

fun CollapsibleHeaderState.nestedScrollConnection(): NestedScrollConnection =
    object : NestedScrollConnection {
        override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
            if (available.y >= 0f) return Offset.Zero
            return Offset(0f, consume(available.y))
        }

        override fun onPostScroll(
            consumed: Offset,
            available: Offset,
            source: NestedScrollSource,
        ): Offset {
            if (available.y <= 0f) return Offset.Zero
            return Offset(0f, consume(available.y))
        }
    }
