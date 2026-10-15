package app.devper.pharm.presentation.stockcount

import app.devper.pharm.presentation.stockcount.exception.StockCountUiStateError
import app.devper.pharm.domain.repository.inventory.StockCountsRepository
import app.devper.pharm.ui.common.BaseLoadableViewModel

class StockCountsListViewModel(
    private val stockCounts: StockCountsRepository,
) : BaseLoadableViewModel<StockCountsListUiState>(StockCountsListUiState()) {

    init { reload() }

    fun onQueryChange(value: String) = setState { copy(query = value) }

    fun reload() {
        setState { copy(loading = counts.isEmpty(), errorState = null) }
        launchResult(
            block = { Result.success(stockCounts.list()) },
            onSuccess = { list -> setState { copy(loading = false, counts = list) } },
            onFailure = { e -> setState { copy(loading = false, errorState = StockCountUiStateError.LoadRoundsFailed(e)) } },
        )
    }
}
