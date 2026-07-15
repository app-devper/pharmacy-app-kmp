package app.devper.pharm.presentation.planning.i18n

import app.devper.pharm.presentation.planning.ReorderSuggestionsMessage
import app.devper.pharm.ui.i18n.PharmStrings

fun ReorderSuggestionsMessage.localizePlanningMessage(s: PharmStrings): String = when (this) {
    is ReorderSuggestionsMessage.Added -> s.planningAddedMessage(count)
}
