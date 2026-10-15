package app.devper.pharm.presentation.reports

import app.devper.pharm.presentation.reports.exception.ReportsUiStateError

import androidx.lifecycle.viewModelScope
import app.devper.pharm.domain.event.StockChangeBus
import app.devper.pharm.domain.observer.SignedInUser
import app.devper.pharm.domain.observer.TimeZoneProvider
import app.devper.pharm.domain.param.reports.DashboardRangeParam
import app.devper.pharm.domain.param.reports.ReportRangeParam
import app.devper.pharm.domain.param.reports.TopOrSlowDrugsParam
import app.devper.pharm.domain.repository.reports.ReportsRepository
import app.devper.pharm.ui.format.startOfMonth
import app.devper.pharm.ui.format.todayLocalDate
import app.devper.pharm.ui.common.BaseLoadableViewModel
import kotlin.time.Clock
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
    private val reports: ReportsRepository,
    private val timeZoneProvider: TimeZoneProvider,
    private val signedInUser: SignedInUser,
    stockChangeBus: StockChangeBus,
    private val now: () -> Long = { Clock.System.now().toEpochMilliseconds() },
) : BaseLoadableViewModel<ReportsUiState>(ReportsUiState()) {

    private var reloadJob: Job? = null

    init {
        viewModelScope.launch {
            val financial = signedInUser.permissions().canSeeFinancialReports
            setState { copy(slowDrugsOnly = !financial) }
            reload()
        }
        stockChangeBus.events
            .debounce(2_000.milliseconds)
            .onEach { reload() }
            .launchIn(viewModelScope)
    }

    fun selectWindow(window: DashboardWindow) {
        setState { copy(window = window) }
        reload()
    }

    fun reload() {
        if (current.slowDrugsOnly) return reloadSlowDrugs()
        val days = current.window.days
        reloadJob?.cancel()
        setState { copy(loading = true, errorState = null) }
        val today = todayLocalDate(timeZoneProvider.current)
        val monthRange = ReportRangeParam(from = today.startOfMonth(), to = today)
        reloadJob = viewModelScope.launch {
            coroutineScope {
                val d = async { catching { reports.dashboard(DashboardRangeParam(days)) } }
                val t = async { catching { reports.topDrugs(TopOrSlowDrugsParam(days)) } }
                val s = async { catching { reports.slowDrugs(TopOrSlowDrugsParam(SLOW_DRUG_DAYS)) } }
                val p = async { catching { reports.profit(monthRange) } }
                val dashboard = d.await()
                val top = t.await()
                val slow = s.await()
                val profit = p.await()
                ensureActive()
                val dashboardError = dashboard.exceptionOrNull()
                val loadedAt = if (dashboard.isSuccess) now() else null
                setState {
                    copy(
                        loading = false,
                        dashboard = dashboard.getOrNull() ?: this.dashboard,
                        monthProfit = profit.getOrNull()?.summary?.profit ?: this.monthProfit,
                        topDrugs = top.getOrNull() ?: this.topDrugs,
                        slowDrugs = slow.getOrNull() ?: this.slowDrugs,
                        lastLoadedAtMillis = loadedAt ?: lastLoadedAtMillis,
                        errorState = dashboardError?.let { ReportsUiStateError.LoadSummaryFailed(it) },
                    )
                }
            }
        }
    }

    private fun reloadSlowDrugs() {
        reloadJob?.cancel()
        setState { copy(loading = true, errorState = null) }
        reloadJob = viewModelScope.launch {
            val slow = catching { reports.slowDrugs(TopOrSlowDrugsParam(SLOW_DRUG_DAYS)) }
            ensureActive()
            val loadedAt = if (slow.isSuccess) now() else null
            setState {
                copy(
                    loading = false,
                    slowDrugs = slow.getOrNull() ?: this.slowDrugs,
                    lastLoadedAtMillis = loadedAt ?: lastLoadedAtMillis,
                    errorState = slow.exceptionOrNull()?.let { ReportsUiStateError.LoadSummaryFailed(it) },
                )
            }
        }
    }

    private companion object {
        const val SLOW_DRUG_DAYS = 90
    }
}
