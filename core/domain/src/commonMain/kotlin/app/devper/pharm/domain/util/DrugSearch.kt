package app.devper.pharm.domain.util

import app.devper.pharm.domain.model.Drug

object DrugSearch {

    fun filter(drugs: List<Drug>, query: String): List<Drug> {
        if (query.isBlank()) return drugs
        val needle = query.trim().lowercase()
        return drugs
            .mapNotNull { drug -> drug.rank(needle)?.let { drug to it } }
            .sortedBy { it.second }
            .map { it.first }
    }

    fun Drug.matches(needle: String): Boolean = rank(needle) != null

    private fun Drug.rank(needle: String): Int? {
        val name = name.lowercase()
        val generic = genericName?.lowercase()
        val barcode = barcode?.lowercase()
        return when {
            name == needle -> 0
            barcode == needle -> 1
            name.startsWith(needle) -> 2
            generic?.startsWith(needle) == true -> 3
            name.contains(needle) -> 4
            generic?.contains(needle) == true -> 5
            barcode?.contains(needle) == true -> 6
            else -> null
        }
    }
}
