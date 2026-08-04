package app.devper.pharm.presentation.reports

import app.devper.pharm.common.error.CommonUiStateMessage

import app.devper.pharm.domain.repository.FakeExportRepository
import app.devper.pharm.domain.repository.FakeReportsRepository
import app.devper.pharm.domain.usecase.reports.ExportProfitCsvUseCase
import app.devper.pharm.domain.usecase.reports.GetProfitReportUseCase
import app.devper.pharm.ui.common.runVmTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertFalse
import kotlin.test.assertNotNull

@OptIn(ExperimentalCoroutinesApi::class)
class ProfitViewModelTest {

    private fun vm(d: app.devper.pharm.common.AppDispatchers) =
        ProfitViewModel(
            GetProfitReportUseCase(FakeReportsRepository(), d),
            ExportProfitCsvUseCase(FakeExportRepository(), d),
            app.devper.pharm.domain.observer.testTimeZoneProvider(),
        )

    @Test
    fun init_loads_report() = runVmTest { d ->
        val model = vm(d)
        advanceUntilIdle()
        assertNotNull(model.state.value.report)
        assertFalse(model.state.value.loading)
    }

    @Test
    fun export_with_no_rows_sets_message() = runVmTest { d ->
        val model = vm(d)
        advanceUntilIdle()
        model.onExportExcel(listOf("h1", "h2"))
        assertIs<CommonUiStateMessage.ExportEmpty>(model.state.value.messageState)
    }

    @Test
    fun date_range_change_updates_both_ends_before_reloading() = runVmTest { d ->
        val model = vm(d)
        advanceUntilIdle()
        val from = app.devper.pharm.ui.format.ymdToMillis("2026-06-01", model.state.value.dateRange.tz)
        val to = app.devper.pharm.ui.format.ymdToMillis("2026-06-30", model.state.value.dateRange.tz)

        model.onDateRangeChange(from, to)
        advanceUntilIdle()

        assertEquals("2026-06-01", model.state.value.dateRange.from)
        assertEquals("2026-06-30", model.state.value.dateRange.to)
        assertFalse(model.state.value.loading)
    }
}
