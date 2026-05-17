package app.devper.pharm.presentation.planning

import androidx.lifecycle.viewModelScope
import app.devper.pharm.domain.event.StockChangeBus
import app.devper.pharm.domain.usecase.GetLowStockDrugsUseCase
import app.devper.pharm.ui.common.BaseViewModel
import kotlinx.coroutines.launch

class LowStockViewModel(
    private val getLowStockDrugs: GetLowStockDrugsUseCase,
    stockChangeBus: StockChangeBus,
) : BaseViewModel<LowStockUiState>(LowStockUiState()) {

    init {
        reload()
        viewModelScope.launch { stockChangeBus.events.collect { reload() } }
    }

    fun dismissError() = setState { copy(error = null) }

    fun reload() {
        setState { copy(loading = true, error = null) }
        launchResult(
            block = { getLowStockDrugs() },
            onSuccess = { list -> setState { copy(loading = false, drugs = list) } },
            onFailure = { e -> setState { copy(loading = false, error = e.message ?: "โหลดข้อมูลไม่สำเร็จ") } },
        )
    }
}
