package app.devper.pharm.presentation.labels

import app.devper.pharm.common.AppException
import app.devper.pharm.domain.model.Drug
import app.devper.pharm.domain.model.LabelLine
import app.devper.pharm.domain.model.LabelSize
import app.devper.pharm.ui.common.LoadableUiState

data class LabelPrintUiState(
    val drugs: List<Drug> = emptyList(),
    val query: String = "",
    val size: LabelSize = LabelSize.Small,
    val lines: List<LabelLine> = emptyList(),
    override val loading: Boolean = false,
    val printing: Boolean = false,
    val message: String? = null,
    val errorState: AppException? = null,
) : LoadableUiState<LabelPrintUiState> {

    override val domainError: AppException? get() = errorState
    override fun withLoading(value: Boolean) = copy(loading = value)
    override fun withDomainError(error: AppException?) = copy(errorState = error)

    val filteredDrugs: List<Drug> = run {
        val q = query.trim().lowercase()
        if (q.isEmpty()) drugs
        else drugs.filter { it.matches(q) }
    }

    val totalCopies: Int = lines.sumOf { it.copies.coerceAtLeast(0) }
    val canPrint: Boolean = !printing && totalCopies > 0
    val previewLine: LabelLine? = lines.firstOrNull()

    private fun Drug.matches(q: String): Boolean =
        name.lowercase().contains(q) ||
            (genericName?.lowercase()?.contains(q) == true) ||
            (barcode?.lowercase()?.contains(q) == true)
}
