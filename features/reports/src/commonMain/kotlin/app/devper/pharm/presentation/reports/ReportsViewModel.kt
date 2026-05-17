package app.devper.pharm.presentation.reports

import androidx.lifecycle.viewModelScope
import app.devper.pharm.domain.event.StockChangeBus
import app.devper.pharm.domain.param.DashboardRangeParam
import app.devper.pharm.domain.usecase.GetDashboardUseCase
import app.devper.pharm.domain.usecase.GetSlowDrugsUseCase
import app.devper.pharm.domain.usecase.GetTopDrugsUseCase
import app.devper.pharm.ui.common.BaseViewModel
import kotlinx.coroutines.launch

class ReportsViewModel(
    private val getDashboard: GetDashboardUseCase,
    private val getTopDrugs: GetTopDrugsUseCase,
    private val getSlowDrugs: GetSlowDrugsUseCase,
    stockChangeBus: StockChangeBus,
) : BaseViewModel<ReportsUiState>(ReportsUiState()) {

    init {
        reload()
        viewModelScope.launch { stockChangeBus.events.collect { reload() } }
    }

    fun selectWindow(window: DashboardWindow) {
        setState { copy(window = window) }
        reload()
    }

    fun dismissError() = setState { copy(error = null) }

    fun reload() {
        val days = current.window.days
        setState { copy(loading = true, error = null) }
        launchResult(
            block = { getDashboard(DashboardRangeParam(days)) },
            onSuccess = { d -> setState { copy(loading = false, dashboard = d) } },
            onFailure = { e -> setState { copy(loading = false, error = e.message ?: "โหลดสรุปไม่สำเร็จ") } },
        )
        launchResult(
            block = { getTopDrugs(days) },
            onSuccess = { list -> setState { copy(topDrugs = list) } },
            onFailure = {  },
        )
        launchResult(

            block = { getSlowDrugs(90) },
            onSuccess = { list -> setState { copy(slowDrugs = list) } },
            onFailure = {  },
        )
    }
}
