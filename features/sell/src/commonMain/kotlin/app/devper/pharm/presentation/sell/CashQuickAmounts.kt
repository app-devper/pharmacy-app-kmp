package app.devper.pharm.presentation.sell

import kotlin.math.ceil

fun cashQuickAmounts(total: Double): List<Int> {
    if (total <= 0.0) return emptyList()
    return listOf(100, 500, 1000)
        .map { note -> (ceil(total / note) * note).toInt() }
        .distinct()
}
