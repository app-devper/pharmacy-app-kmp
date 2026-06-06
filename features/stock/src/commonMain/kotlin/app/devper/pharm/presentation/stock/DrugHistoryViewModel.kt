package app.devper.pharm.presentation.stock

import app.devper.pharm.domain.param.MovementsFilterParam
import app.devper.pharm.domain.usecase.GetMovementsUseCase
import app.devper.pharm.ui.common.BaseViewModel

class DrugHistoryViewModel(
    private val getMovements: GetMovementsUseCase,
) : BaseViewModel<DrugHistoryUiState>(DrugHistoryUiState()) {

    fun load(drugName: String) {
        setState { copy(drugName = drugName, loading = true, error = null) }
        launchResult(
            block = { getMovements(MovementsFilterParam(drugName = drugName, limit = 200)) },
            onSuccess = { page -> setState { copy(loading = false, items = page.items) } },
            onFailure = { e -> setState { copy(loading = false, error = e.message ?: "โหลดประวัติไม่สำเร็จ") } },
        )
    }

    fun dismissError() = setState { copy(error = null) }
}
