package app.devper.pharm.presentation.planning

import app.devper.pharm.common.AppException
import app.devper.pharm.domain.model.Drug
import app.devper.pharm.ui.common.LoadableUiState

data class LowStockUiState(
    override val loading: Boolean = false,
    val drugs: List<Drug> = emptyList(),
    val errorState: AppException? = null,
) : LoadableUiState<LowStockUiState> {

    override val domainError: AppException? get() = errorState
    override fun withLoading(value: Boolean) = copy(loading = value)
    override fun withDomainError(error: AppException?) = copy(errorState = error)
}
