package app.devper.pharm.presentation.planning

import androidx.lifecycle.viewModelScope
import app.devper.pharm.domain.event.StockChangeBus
import app.devper.pharm.domain.usecase.GetLowStockDrugsUseCase
import app.devper.pharm.ui.common.BaseViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class LowStockViewModel(
    private val getLowStockDrugs: GetLowStockDrugsUseCase,
    stockChangeBus: StockChangeBus,
) : BaseViewModel<LowStockUiState>(LowStockUiState()) {

    init {
        reload()
        stockChangeBus.events
            .onEach { reload() }
            .catch { e -> setState { copy(error = e.message ?: "ติดตามการเปลี่ยนแปลงสต็อกไม่สำเร็จ") } }
            .launchIn(viewModelScope)
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
