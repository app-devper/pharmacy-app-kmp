package app.devper.pharm.presentation.stock

import app.devper.pharm.domain.param.reports.MovementsFilterParam
import app.devper.pharm.domain.repository.reports.MovementsRepository
import app.devper.pharm.presentation.stock.exception.StockUiStateError
import app.devper.pharm.ui.common.BaseViewModel

class DrugHistoryViewModel(
    private val movements: MovementsRepository,
) : BaseViewModel<DrugHistoryUiState>(DrugHistoryUiState()) {

    fun load(drugName: String) {
        setState { copy(drugName = drugName, loading = true, errorState = null) }
        launchResult(
            block = { Result.success(movements.list(MovementsFilterParam(drugName = drugName, limit = 200))) },
            onSuccess = { page -> setState { copy(loading = false, items = page.items) } },
            onFailure = { e -> setState { copy(loading = false, errorState = StockUiStateError.LoadHistoryFailed(e)) } },
        )
    }

    fun dismissError() = setState { copy(errorState = null) }
}
