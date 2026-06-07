package app.devper.pharm.presentation.stock

import app.devper.pharm.domain.usecase.GetDrugsUseCase
import app.devper.pharm.ui.common.BaseLoadableViewModel

class StockViewModel(
    private val getDrugs: GetDrugsUseCase,
) : BaseLoadableViewModel<StockUiState>(StockUiState()) {

    init { reload() }

    fun onQueryChange(value: String) = setState { copy(query = value) }
    fun onTypeFilterChange(value: StockTypeFilter) = setState { copy(typeFilter = value) }

    fun reload() = launchLoad(
        block = { getDrugs() },
        onSuccess = { list -> copy(drugs = list) },
    )
}
