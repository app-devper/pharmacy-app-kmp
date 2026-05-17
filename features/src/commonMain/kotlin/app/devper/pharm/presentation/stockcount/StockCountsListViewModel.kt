package app.devper.pharm.presentation.stockcount

import app.devper.pharm.domain.usecase.GetStockCountsUseCase
import app.devper.pharm.ui.common.BaseViewModel

class StockCountsListViewModel(
    private val getStockCounts: GetStockCountsUseCase,
) : BaseViewModel<StockCountsListUiState>(StockCountsListUiState()) {

    init { reload() }

    fun onQueryChange(value: String) = setState { copy(query = value) }
    fun dismissError() = setState { copy(error = null) }

    fun reload() {
        setState { copy(loading = true, error = null) }
        launchResult(
            block = { getStockCounts() },
            onSuccess = { list -> setState { copy(loading = false, counts = list) } },
            onFailure = { e -> setState { copy(loading = false, error = e.message ?: "โหลดข้อมูลไม่สำเร็จ") } },
        )
    }
}
