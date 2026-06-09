package app.devper.pharm.presentation.help.i18n

import app.devper.pharm.presentation.help.exception.HelpUiStateError
import app.devper.pharm.ui.i18n.PharmStrings

fun HelpUiStateError.localize(s: PharmStrings): String = when (this) {
    is HelpUiStateError.LoadFailed -> s.helpLoadFailed
}
