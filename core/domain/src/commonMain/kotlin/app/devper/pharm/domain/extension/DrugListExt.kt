package app.devper.pharm.domain.extension

import app.devper.pharm.domain.model.Drug

fun List<Drug>.searchByQuery(query: String): List<Drug> {
    if (query.isBlank()) return this
    val needle = query.trim()
    val ranked = ArrayList<Pair<Drug, Int>>(size)
    for (drug in this) {
        val rank = drug.rank(needle) ?: continue
        ranked.add(drug to rank)
    }
    ranked.sortBy { it.second }
    return ranked.map { it.first }
}

fun Drug.matchesQuery(needle: String): Boolean = rank(needle) != null

private fun Drug.rank(needle: String): Int? = when {
    name.equals(needle, ignoreCase = true) -> 0
    barcode?.equals(needle, ignoreCase = true) == true -> 1
    name.startsWith(needle, ignoreCase = true) -> 2
    genericName?.startsWith(needle, ignoreCase = true) == true -> 3
    name.contains(needle, ignoreCase = true) -> 4
    genericName?.contains(needle, ignoreCase = true) == true -> 5
    barcode?.contains(needle, ignoreCase = true) == true -> 6
    else -> null
}
