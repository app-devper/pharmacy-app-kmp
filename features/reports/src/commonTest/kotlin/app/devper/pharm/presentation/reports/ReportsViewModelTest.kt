package app.devper.pharm.presentation.reports

import app.devper.pharm.common.NetworkException
import app.devper.pharm.domain.event.StockChangeBus
import app.devper.pharm.domain.model.Role
import app.devper.pharm.domain.repository.FakeProfileRepository
import app.devper.pharm.domain.usecase.profile.GetProfileUseCase
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
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class ReportsViewModelTest {

    private fun vm(
        d: app.devper.pharm.common.AppDispatchers,
        repo: FakeReportsRepository = FakeReportsRepository(),
        role: Role = Role.ADMIN,
        now: () -> Long = { 1_000L },
    ): ReportsViewModel =
        ReportsViewModel(
            GetDashboardUseCase(repo, d),
            GetTopDrugsUseCase(repo, d),
            GetSlowDrugsUseCase(repo, d),
            GetProfitReportUseCase(repo, d),
            testTimeZoneProvider(),
            GetProfileUseCase(FakeProfileRepository(FakeProfileRepository.sampleUser.copy(role = role)), d),
            StockChangeBus(),
            now,
        )

    @Test
    fun manager_loads_only_slow_drugs() = runVmTest { d ->
        val repo = FakeReportsRepository()
        val model = vm(d, repo, role = Role.MANAGER)
        advanceUntilIdle()
        assertTrue(model.state.value.slowDrugsOnly)
        assertNull(model.state.value.dashboard)
        assertEquals(0, repo.dashboardCalls)
        assertEquals(1, repo.slowDrugsCalls)
        assertFalse(model.state.value.loading)
    }

    @Test
    fun admin_loads_the_full_dashboard() = runVmTest { d ->
        val model = vm(d, role = Role.ADMIN)
        advanceUntilIdle()
        assertFalse(model.state.value.slowDrugsOnly)
        assertNotNull(model.state.value.dashboard)
    }

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

    @Test
    fun a_successful_load_records_when_the_data_was_confirmed() = runVmTest { d ->
        val model = vm(d, now = { 5_000L })
        advanceUntilIdle()
        assertEquals(5_000L, model.state.value.lastLoadedAtMillis)
        assertFalse(model.state.value.refreshFailed)
    }

    @Test
    fun a_failed_refresh_keeps_old_values_and_their_confirmed_time() = runVmTest { d ->
        val repo = FakeReportsRepository()
        var clock = 5_000L
        val model = vm(d, repo, now = { clock })
        advanceUntilIdle()
        repo.dashboardThrows = NetworkException()
        clock = 9_000L
        model.reload()
        advanceUntilIdle()
        assertNotNull(model.state.value.dashboard)
        assertEquals(5_000L, model.state.value.lastLoadedAtMillis)
        assertTrue(model.state.value.refreshFailed)
    }

    @Test
    fun a_first_load_failure_has_no_confirmed_time() = runVmTest { d ->
        val repo = FakeReportsRepository().apply { dashboardThrows = NetworkException() }
        val model = vm(d, repo)
        advanceUntilIdle()
        assertNull(model.state.value.lastLoadedAtMillis)
        assertFalse(model.state.value.refreshFailed)
    }

    @Test
    fun manager_slow_drugs_load_records_its_time() = runVmTest { d ->
        val model = vm(d, role = Role.MANAGER, now = { 7_000L })
        advanceUntilIdle()
        assertEquals(7_000L, model.state.value.lastLoadedAtMillis)
    }
}
