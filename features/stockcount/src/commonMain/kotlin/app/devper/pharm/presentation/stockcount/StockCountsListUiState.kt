package app.devper.pharm.presentation.stockcount

import app.devper.pharm.common.AppException
import app.devper.pharm.domain.model.StockCount
import app.devper.pharm.ui.common.LoadableUiState

data class StockCountsListUiState(
    override val loading: Boolean = false,
    val query: String = "",
    val counts: List<StockCount> = emptyList(),
    val errorState: AppException? = null,
) : LoadableUiState<StockCountsListUiState> {

    override val domainError: AppException? get() = errorState
    override fun withLoading(value: Boolean) = copy(loading = value)
    override fun withDomainError(error: AppException?) = copy(errorState = error)

    val filtered: List<StockCount> = if (query.isBlank()) {
        counts
    } else {
        val q = query.trim().lowercase()
        counts.filter { c ->
            c.countNo.lowercase().contains(q) ||
                c.note.lowercase().contains(q)
        }
    }
}
