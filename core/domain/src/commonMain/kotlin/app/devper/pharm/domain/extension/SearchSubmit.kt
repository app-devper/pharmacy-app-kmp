package app.devper.pharm.domain.extension

import app.devper.pharm.domain.model.Drug

sealed interface SearchSubmitAction {
    data class AddNow(val drug: Drug) : SearchSubmitAction
    data class Confirm(val drug: Drug) : SearchSubmitAction
    data object None : SearchSubmitAction
}

fun List<Drug>.resolveSearchSubmit(query: String): SearchSubmitAction {
    val needle = query.trim()
    if (needle.isEmpty()) return SearchSubmitAction.None
    val exactBarcode = firstOrNull { it.barcode?.equals(needle, ignoreCase = true) == true }
    return when {
        exactBarcode != null -> SearchSubmitAction.AddNow(exactBarcode)
        isEmpty() -> SearchSubmitAction.None
        size == 1 -> SearchSubmitAction.AddNow(first())
        else -> SearchSubmitAction.Confirm(first())
    }
}
