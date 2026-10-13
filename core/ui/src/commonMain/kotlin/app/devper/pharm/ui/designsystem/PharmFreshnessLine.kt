package app.devper.pharm.ui.designsystem

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.devper.pharm.ui.format.millisToBuddhistDisplayWithTime
import app.devper.pharm.ui.i18n.pharmStrings
import app.devper.pharm.ui.theme.PharmText
import app.devper.pharm.ui.theme.pharmTokens

sealed interface Freshness {
    data object Unknown : Freshness
    data class Confirmed(val atMillis: Long) : Freshness
    data class Stale(val atMillis: Long) : Freshness
}

fun freshnessOf(lastLoadedAtMillis: Long?, refreshFailed: Boolean): Freshness = when {
    lastLoadedAtMillis == null -> Freshness.Unknown
    refreshFailed -> Freshness.Stale(lastLoadedAtMillis)
    else -> Freshness.Confirmed(lastLoadedAtMillis)
}

@Composable
fun PharmFreshnessLine(
    lastLoadedAtMillis: Long?,
    refreshFailed: Boolean,
    modifier: Modifier = Modifier,
) {
    val t = pharmTokens
    val s = pharmStrings
    val (text, color) = when (val freshness = freshnessOf(lastLoadedAtMillis, refreshFailed)) {
        Freshness.Unknown -> return
        is Freshness.Confirmed ->
            s.commonLastUpdated(millisToBuddhistDisplayWithTime(freshness.atMillis)) to t.colors.fg3
        is Freshness.Stale ->
            s.commonRefreshFailedShowing(millisToBuddhistDisplayWithTime(freshness.atMillis)) to t.colors.warningFg
    }
    Text(
        text = text,
        style = PharmText.meta.copy(color = color),
        modifier = modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 6.dp),
    )
}
