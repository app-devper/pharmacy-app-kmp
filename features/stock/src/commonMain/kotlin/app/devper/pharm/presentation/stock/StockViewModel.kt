package app.devper.pharm.presentation.stock

import app.devper.pharm.common.error.ErrorMessages

import app.devper.pharm.domain.usecase.GetDrugsUseCase
import app.devper.pharm.ui.common.BaseViewModel

class StockViewModel(
    private val getDrugs: GetDrugsUseCase,
) : BaseViewModel<StockUiState>(StockUiState()) {

    init { reload() }

    fun onQueryChange(value: String) = setState { copy(query = value) }
    fun onTypeFilterChange(value: StockTypeFilter) = setState { copy(typeFilter = value) }
    fun dismissError() = setState { copy(error = null) }

    fun reload() {
        setState { copy(loading = true, error = null) }
        launchResult(
            block = { getDrugs() },
            onSuccess = { list -> setState { copy(loading = false, drugs = list) } },
            onFailure = { e -> setState { copy(loading = false, error = e.message ?: ErrorMessages.LOAD_FAILED) } },
        )
    }
}
