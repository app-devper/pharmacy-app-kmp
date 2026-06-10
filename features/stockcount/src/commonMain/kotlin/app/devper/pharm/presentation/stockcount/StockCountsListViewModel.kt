package app.devper.pharm.presentation.stockcount

import app.devper.pharm.common.error.CommonUiStateError
import app.devper.pharm.domain.usecase.inventory.GetStockCountsUseCase
import app.devper.pharm.ui.common.BaseLoadableViewModel

class StockCountsListViewModel(
    private val getStockCounts: GetStockCountsUseCase,
) : BaseLoadableViewModel<StockCountsListUiState>(StockCountsListUiState()) {

    init { reload() }

    fun onQueryChange(value: String) = setState { copy(query = value) }

    fun reload() {
        setState { copy(loading = true, errorState = null) }
        launchResult(
            block = { getStockCounts() },
            onSuccess = { list -> setState { copy(loading = false, counts = list) } },
            onFailure = { e -> setState { copy(loading = false, errorState = CommonUiStateError.LoadFailed(e)) } },
        )
    }
}
