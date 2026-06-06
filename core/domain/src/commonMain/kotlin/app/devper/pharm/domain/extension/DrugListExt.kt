package app.devper.pharm.domain.extension

import app.devper.pharm.domain.model.Drug

fun List<Drug>.searchByQuery(query: String): List<Drug> {
    if (query.isBlank()) return this
    val needle = query.trim().lowercase()
    return this
        .mapNotNull { drug -> drug.rank(needle)?.let { drug to it } }
        .sortedBy { it.second }
        .map { it.first }
}

fun Drug.matchesQuery(needle: String): Boolean = rank(needle) != null

private fun Drug.rank(needle: String): Int? {
    val nameLower = name.lowercase()
    val generic = genericName?.lowercase()
    val barcodeLower = barcode?.lowercase()
    return when {
        nameLower == needle -> 0
        barcodeLower == needle -> 1
        nameLower.startsWith(needle) -> 2
        generic?.startsWith(needle) == true -> 3
        nameLower.contains(needle) -> 4
        generic?.contains(needle) == true -> 5
        barcodeLower?.contains(needle) == true -> 6
        else -> null
    }
}
