package app.devper.pharm.presentation.planning

import androidx.lifecycle.viewModelScope
import app.devper.pharm.common.error.CommonUiStateError
import app.devper.pharm.domain.event.StockChangeBus
import app.devper.pharm.domain.usecase.GetLowStockDrugsUseCase
import app.devper.pharm.presentation.planning.exception.LowStockUiStateError
import app.devper.pharm.ui.common.BaseLoadableViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class LowStockViewModel(
    private val getLowStockDrugs: GetLowStockDrugsUseCase,
    stockChangeBus: StockChangeBus,
) : BaseLoadableViewModel<LowStockUiState>(LowStockUiState()) {

    init {
        reload()
        stockChangeBus.events
            .onEach { reload() }
            .catch { e -> setState { copy(errorState = LowStockUiStateError.TrackStockFailed(e)) } }
            .launchIn(viewModelScope)
    }

    fun reload() {
        setState { copy(loading = true, errorState = null) }
        launchResult(
            block = { getLowStockDrugs() },
            onSuccess = { list -> setState { copy(loading = false, drugs = list) } },
            onFailure = { e -> setState { copy(loading = false, errorState = CommonUiStateError.LoadFailed(e)) } },
        )
    }
}
