package app.devper.pharm.presentation.stock

import app.devper.pharm.domain.model.Drug
import app.devper.pharm.domain.extension.searchByQuery
import app.devper.pharm.common.AppException
import app.devper.pharm.ui.common.LoadableUiState

data class StockUiState(
    override val loading: Boolean = false,
    val query: String = "",
    val typeFilter: StockTypeFilter = StockTypeFilter.All,
    val drugs: List<Drug> = emptyList(),
    val expiringSoonCount: Int? = null,
    val errorState: AppException? = null,
) : LoadableUiState<StockUiState> {

    override fun withLoading(value: Boolean) = copy(loading = value)
    override val domainError: AppException? get() = errorState
    override fun withDomainError(error: AppException?) = copy(errorState = error)

    val filtered: List<Drug> = drugs.searchByQuery(query).filter { typeFilter.matches(it.type) }
}
