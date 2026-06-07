package app.devper.pharm.ui.i18n

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider

@Composable
fun AppLocaleProvider(
    localeWire: String?,
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(LocalPharmStrings provides resolvePharmStrings(localeWire), content = content)
}

internal fun resolvePharmStrings(localeWire: String?): PharmStrings =
    if (localeWire?.lowercase() == "en") PharmStringsEn else PharmStringsTh
