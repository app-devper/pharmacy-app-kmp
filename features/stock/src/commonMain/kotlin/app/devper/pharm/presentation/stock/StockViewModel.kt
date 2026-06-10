package app.devper.pharm.presentation.stock

import app.devper.pharm.common.error.CommonUiStateError
import app.devper.pharm.domain.usecase.inventory.GetDrugsUseCase
import app.devper.pharm.ui.common.BaseLoadableViewModel

class StockViewModel(
    private val getDrugs: GetDrugsUseCase,
) : BaseLoadableViewModel<StockUiState>(StockUiState()) {

    init { reload() }

    fun onQueryChange(value: String) = setState { copy(query = value) }
    fun onTypeFilterChange(value: StockTypeFilter) = setState { copy(typeFilter = value) }

    fun reload() {
        setState { copy(loading = true, errorState = null) }
        launchResult(
            block = { getDrugs() },
            onSuccess = { list -> setState { copy(loading = false, drugs = list) } },
            onFailure = { e -> setState { copy(loading = false, errorState = CommonUiStateError.LoadFailed(e)) } },
        )
    }
}
