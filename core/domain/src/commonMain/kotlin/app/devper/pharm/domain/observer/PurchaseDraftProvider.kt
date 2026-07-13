package app.devper.pharm.domain.observer

import app.devper.pharm.domain.model.PurchaseDraftLine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class PurchaseDraftProvider {
    private val lines = MutableStateFlow<List<PurchaseDraftLine>>(emptyList())
    val state: StateFlow<List<PurchaseDraftLine>> = lines.asStateFlow()

    fun addUnique(newLines: List<PurchaseDraftLine>) {
        val existingIds = lines.value.map { it.drugId }.toSet()
        lines.value = lines.value + newLines.filter { it.drugId !in existingIds }
    }

    fun consume(): List<PurchaseDraftLine> {
        val current = lines.value
        lines.value = emptyList()
        return current
    }

    fun clear() {
        lines.value = emptyList()
    }
}
