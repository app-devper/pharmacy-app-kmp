package app.devper.pharm.presentation.reports

import app.devper.pharm.domain.event.StockChangeBus
import app.devper.pharm.domain.model.ProfitReport
import app.devper.pharm.domain.model.ProfitSummary
import app.devper.pharm.domain.observer.testTimeZoneProvider
import app.devper.pharm.domain.repository.FakeReportsRepository
import app.devper.pharm.domain.usecase.reports.GetDashboardUseCase
import app.devper.pharm.domain.usecase.reports.GetProfitReportUseCase
import app.devper.pharm.domain.usecase.reports.GetSlowDrugsUseCase
import app.devper.pharm.domain.usecase.reports.GetTopDrugsUseCase
import app.devper.pharm.ui.common.runVmTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull

@OptIn(ExperimentalCoroutinesApi::class)
class ReportsViewModelTest {

    private fun vm(
        d: app.devper.pharm.common.AppDispatchers,
        repo: FakeReportsRepository = FakeReportsRepository(),
    ): ReportsViewModel =
        ReportsViewModel(
            GetDashboardUseCase(repo, d),
            GetTopDrugsUseCase(repo, d),
            GetSlowDrugsUseCase(repo, d),
            GetProfitReportUseCase(repo, d),
            testTimeZoneProvider(),
            StockChangeBus(),
        )

    @Test
    fun init_loads_dashboard() = runVmTest { d ->
        val model = vm(d)
        advanceUntilIdle()
        assertFalse(model.state.value.loading)
        assertNotNull(model.state.value.dashboard)
    }

    @Test
    fun loads_real_month_profit_from_the_profit_report() = runVmTest { d ->
        val repo = FakeReportsRepository().apply {
            profitReport = ProfitReport(
                summary = ProfitSummary(revenue = 5000.0, cost = 3200.0, profit = 1800.0, margin = 36.0, bills = 12),
                byDrug = emptyList(),
            )
        }
        val model = vm(d, repo)
        advanceUntilIdle()
        assertEquals(1800.0, model.state.value.monthProfit)
        assertNotNull(repo.lastProfitParam?.from)
    }

    @Test
    fun select_window_updates_and_reloads() = runVmTest { d ->
        val model = vm(d)
        advanceUntilIdle()
        model.selectWindow(DashboardWindow.Last7)
        advanceUntilIdle()
        assertEquals(DashboardWindow.Last7, model.state.value.window)
        assertFalse(model.state.value.loading)
    }
}
