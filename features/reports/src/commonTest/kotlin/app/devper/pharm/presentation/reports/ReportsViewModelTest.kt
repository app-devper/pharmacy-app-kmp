package app.devper.pharm.presentation.reports

import app.devper.pharm.domain.event.StockChangeBus
import app.devper.pharm.domain.repository.FakeReportsRepository
import app.devper.pharm.domain.usecase.GetDashboardUseCase
import app.devper.pharm.domain.usecase.GetSlowDrugsUseCase
import app.devper.pharm.domain.usecase.GetTopDrugsUseCase
import app.devper.pharm.ui.common.runVmTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull

@OptIn(ExperimentalCoroutinesApi::class)
class ReportsViewModelTest {

    private fun vm(d: app.devper.pharm.common.AppDispatchers): ReportsViewModel {
        val repo = FakeReportsRepository()
        return ReportsViewModel(
            GetDashboardUseCase(repo, d),
            GetTopDrugsUseCase(repo, d),
            GetSlowDrugsUseCase(repo, d),
            StockChangeBus(),
        )
    }

    @Test
    fun init_loads_dashboard() = runVmTest { d ->
        val model = vm(d)
        advanceUntilIdle()
        assertFalse(model.state.value.loading)
        assertNotNull(model.state.value.dashboard)
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
