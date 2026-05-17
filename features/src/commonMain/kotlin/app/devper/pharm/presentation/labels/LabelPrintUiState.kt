package app.devper.pharm.presentation.labels

import app.devper.pharm.domain.model.Drug
import app.devper.pharm.domain.model.LabelLine
import app.devper.pharm.domain.model.LabelSize
import app.devper.pharm.ui.common.BaseUiState

data class LabelPrintUiState(
    val drugs: List<Drug> = emptyList(),
    val query: String = "",
    val size: LabelSize = LabelSize.Small,
    val lines: List<LabelLine> = emptyList(),
    override val loading: Boolean = false,
    val printing: Boolean = false,
    val message: String? = null,
    override val error: String? = null,
) : BaseUiState {

    val filteredDrugs: List<Drug>
        get() {
            val q = query.trim().lowercase()
            return if (q.isEmpty()) drugs
            else drugs.filter { it.matches(q) }
        }

    val totalCopies: Int get() = lines.sumOf { it.copies.coerceAtLeast(0) }
    val canPrint: Boolean get() = !printing && totalCopies > 0
    val previewLine: LabelLine? get() = lines.firstOrNull()

    private fun Drug.matches(q: String): Boolean =
        name.lowercase().contains(q) ||
            (genericName?.lowercase()?.contains(q) == true) ||
            (barcode?.lowercase()?.contains(q) == true)
}
