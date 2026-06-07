package app.devper.pharm.presentation.reports

import androidx.lifecycle.viewModelScope
import app.devper.pharm.domain.event.StockChangeBus
import app.devper.pharm.domain.param.DashboardRangeParam
import app.devper.pharm.domain.usecase.GetDashboardUseCase
import app.devper.pharm.domain.usecase.GetSlowDrugsUseCase
import app.devper.pharm.domain.usecase.GetTopDrugsUseCase
import app.devper.pharm.ui.common.BaseViewModel
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@OptIn(FlowPreview::class)
class ReportsViewModel(
    private val getDashboard: GetDashboardUseCase,
    private val getTopDrugs: GetTopDrugsUseCase,
    private val getSlowDrugs: GetSlowDrugsUseCase,
    stockChangeBus: StockChangeBus,
) : BaseViewModel<ReportsUiState>(ReportsUiState()) {

    private var reloadJob: Job? = null

    init {
        reload()
        stockChangeBus.events
            .debounce(2_000.milliseconds)
            .onEach { reload() }
            .launchIn(viewModelScope)
    }

    fun selectWindow(window: DashboardWindow) {
        setState { copy(window = window) }
        reload()
    }

    fun dismissError() = setState { copy(error = null) }

    fun reload() {
        val days = current.window.days
        reloadJob?.cancel()
        setState { copy(loading = true, error = null) }
        reloadJob = viewModelScope.launch {
            val (dashboard, top, slow) = coroutineScope {
                val d = async { getDashboard(DashboardRangeParam(days)) }
                val t = async { getTopDrugs(days) }
                val s = async { getSlowDrugs(90) }
                Triple(d.await(), t.await(), s.await())
            }
            ensureActive()
            val dashboardError = dashboard.exceptionOrNull()
            setState {
                copy(
                    loading = false,
                    dashboard = dashboard.getOrNull() ?: this.dashboard,
                    topDrugs = top.getOrNull() ?: this.topDrugs,
                    slowDrugs = slow.getOrNull() ?: this.slowDrugs,
                    error = dashboardError?.let { it.message ?: "โหลดสรุปไม่สำเร็จ" },
                )
            }
        }
    }
}
