package app.devper.pharm.ui.i18n

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.text.intl.Locale

@Composable
fun AppLocaleProvider(
    localeWire: String?,
    content: @Composable () -> Unit,
) {
    val strings = resolvePharmStrings(localeWire)
    CompositionLocalProvider(LocalPharmStrings provides strings, content = content)
}

@Composable
internal fun resolvePharmStrings(localeWire: String?): PharmStrings = when (localeWire?.lowercase()) {
    "th"   -> PharmStringsTh
    "en"   -> PharmStringsEn
    "system", null, "" -> if (Locale.current.language.startsWith("en", ignoreCase = true)) {
        PharmStringsEn
    } else {
        PharmStringsTh
    }
    else   -> PharmStringsTh
}
