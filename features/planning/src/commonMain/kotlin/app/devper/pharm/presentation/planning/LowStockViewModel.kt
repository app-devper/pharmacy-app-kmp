package app.devper.pharm.presentation.planning

import androidx.lifecycle.viewModelScope
import app.devper.pharm.domain.event.StockChangeBus
import app.devper.pharm.domain.repository.inventory.DrugRepository
import app.devper.pharm.presentation.planning.exception.LowStockUiStateError
import app.devper.pharm.ui.common.BaseLoadableViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class LowStockViewModel(
    private val drugs: DrugRepository,
    stockChangeBus: StockChangeBus,
) : BaseLoadableViewModel<LowStockUiState>(LowStockUiState()) {

    init {
        reload()
        stockChangeBus.events
            .onEach { reload() }
            .catch { e -> setState { copy(errorState = LowStockUiStateError.TrackStockFailed(e)) } }
            .launchIn(viewModelScope)
    }

    fun onQueryChange(value: String) = setState { copy(query = value) }

    fun reload() {
        load(
            fetch = { Result.success(drugs.lowStock()) },
            failure = { e -> LowStockUiStateError.LoadFailed(e) },
            onLoaded = { list -> copy(drugs = list) },
        )
    }
}
