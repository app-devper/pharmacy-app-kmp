package app.devper.pharm.presentation.planning

import androidx.lifecycle.viewModelScope
import app.devper.pharm.domain.event.StockChangeBus
import app.devper.pharm.domain.usecase.GetLowStockDrugsUseCase
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
            .catch { e -> setState { copy(error = e.message ?: "ติดตามการเปลี่ยนแปลงสต็อกไม่สำเร็จ") } }
            .launchIn(viewModelScope)
    }

    fun reload() = launchLoad(
        block = { getLowStockDrugs() },
        onSuccess = { list -> copy(drugs = list) },
    )
}
