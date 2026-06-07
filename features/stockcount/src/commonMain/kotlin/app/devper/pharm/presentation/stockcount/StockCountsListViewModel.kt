package app.devper.pharm.presentation.stockcount

import app.devper.pharm.domain.usecase.GetStockCountsUseCase
import app.devper.pharm.ui.common.BaseLoadableViewModel

class StockCountsListViewModel(
    private val getStockCounts: GetStockCountsUseCase,
) : BaseLoadableViewModel<StockCountsListUiState>(StockCountsListUiState()) {

    init { reload() }

    fun onQueryChange(value: String) = setState { copy(query = value) }

    fun reload() = launchLoad(
        block = { getStockCounts() },
        onSuccess = { list -> copy(counts = list) },
    )
}
