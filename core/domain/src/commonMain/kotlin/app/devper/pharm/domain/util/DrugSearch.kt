package app.devper.pharm.domain.util

import app.devper.pharm.domain.model.Drug

object DrugSearch {

    fun filter(drugs: List<Drug>, query: String): List<Drug> {
        if (query.isBlank()) return drugs
        val needle = query.trim().lowercase()
        return drugs.filter { it.matches(needle) }
    }

    fun Drug.matches(needle: String): Boolean =
        name.lowercase().contains(needle) ||
            (genericName?.lowercase()?.contains(needle) == true) ||
            (barcode?.lowercase()?.contains(needle) == true)
}
