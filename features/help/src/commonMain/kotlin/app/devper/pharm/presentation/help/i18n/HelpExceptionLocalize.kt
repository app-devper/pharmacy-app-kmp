package app.devper.pharm.presentation.help.i18n

import app.devper.pharm.presentation.help.exception.HelpException
import app.devper.pharm.ui.i18n.PharmStrings

fun HelpException.localize(s: PharmStrings): String = when (this) {
    is HelpException.Markdown.LoadFailed -> s.helpLoadFailed
}
