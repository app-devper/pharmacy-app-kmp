package app.devper.pharm.presentation.reports

import app.devper.pharm.presentation.reports.exception.EodUiStateError

import app.devper.pharm.common.AppDispatchers
import app.devper.pharm.common.print.ReceiptPrinter
import app.devper.pharm.common.print.ReceiptTemplate
import app.devper.pharm.domain.model.EodCloseResult
import app.devper.pharm.domain.model.EodReport
import app.devper.pharm.domain.observer.SettingsProvider
import app.devper.pharm.domain.repository.FakeReportsRepository
import app.devper.pharm.domain.repository.FakeSettingsRepository
import app.devper.pharm.domain.usecase.reports.CloseEodUseCase
import app.devper.pharm.domain.usecase.reports.GetEodReportUseCase
import app.devper.pharm.domain.usecase.reports.PrintReceiptUseCase
import app.devper.pharm.ui.common.runVmTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

private class RecordingPrinter(private val result: Boolean = true) : ReceiptPrinter {
    var lastTemplate: ReceiptTemplate? = null
        private set
    var calls: Int = 0
        private set

    override fun print(template: ReceiptTemplate): Boolean {
        calls++
        lastTemplate = template
        return result
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
class EodViewModelTest {

    private val sampleReport = EodReport(
        date = kotlinx.datetime.LocalDate.parse("2026-05-19"),
        billCount = 4,
        totalSales = 3850.0,
        totalDiscount = 70.0,
        totalReceived = 4000.0,
        totalChange = 150.0,
        netCash = 3850.0,
        bills = emptyList(),
    )

    private val sampleCloseResult = EodCloseResult(
        closeId = "eod-2026-05-19",
        date = kotlinx.datetime.LocalDate.parse("2026-05-19"),
        closedAt = kotlinx.datetime.LocalDateTime.parse("2026-05-19T23:59:00"),
        closedBy = "cashier-01",
        report = sampleReport,
    )

    private fun newVm(
        dispatchers: AppDispatchers,
        reports: FakeReportsRepository = FakeReportsRepository(
            eodResult = sampleReport,
            closeResult = sampleCloseResult,
        ),
        settings: FakeSettingsRepository = FakeSettingsRepository(),
        printer: ReceiptPrinter = RecordingPrinter(),
    ): EodViewModel = EodViewModel(
        settings = SettingsProvider(settings),
        getEodReport = GetEodReportUseCase(reports, dispatchers),
        closeEod = CloseEodUseCase(reports, dispatchers),
        printReceiptUseCase = PrintReceiptUseCase(printer, dispatchers),
    )

    @Test
    fun init_loads_report_from_repository() = runVmTest { dispatchers ->
        val vm = newVm(dispatchers)
        advanceUntilIdle()
        assertEquals(sampleReport, vm.state.value.report)
        assertFalse(vm.state.value.loading)
        assertNull(vm.state.value.errorState)
    }

    @Test
    fun confirmCloseDay_success_marks_closed_and_stores_result() = runVmTest { dispatchers ->
        val reports = FakeReportsRepository(
            eodResult = sampleReport,
            closeResult = sampleCloseResult,
        )
        val vm = newVm(dispatchers, reports = reports)
        advanceUntilIdle()
        vm.onDateChange("2026-05-19")
        vm.applyDate()
        advanceUntilIdle()

        vm.requestCloseDay()
        assertTrue(vm.state.value.confirmClose)

        vm.confirmCloseDay()
        advanceUntilIdle()

        assertEquals(1, reports.closeCallCount)
        assertEquals(kotlinx.datetime.LocalDate.parse("2026-05-19"), reports.lastCloseParam?.date)
        assertFalse(vm.state.value.confirmClose)
        assertFalse(vm.state.value.closing)
        assertTrue(vm.state.value.closed)
        assertEquals(sampleCloseResult, vm.state.value.closeResult)
        assertEquals(sampleReport, vm.state.value.report)
        assertNull(vm.state.value.errorState)
    }

    @Test
    fun confirmCloseDay_failure_does_not_set_closed_and_surfaces_error() = runVmTest { dispatchers ->
        val reports = FakeReportsRepository(
            eodResult = sampleReport,
            closeThrows = RuntimeException("ปิดยอดไม่สำเร็จ — ลองใหม่"),
        )
        val vm = newVm(dispatchers, reports = reports)
        advanceUntilIdle()
        vm.requestCloseDay()
        vm.confirmCloseDay()
        advanceUntilIdle()

        assertFalse(vm.state.value.closed)
        assertNull(vm.state.value.closeResult)
        assertFalse(vm.state.value.closing)
        assertFalse(vm.state.value.confirmClose)
        assertIs<EodUiStateError.CloseFailed>(vm.state.value.errorState)
        assertEquals("ปิดยอดไม่สำเร็จ — ลองใหม่", vm.state.value.errorState?.cause?.message)
    }

    @Test
    fun printReceipt_no_op_when_no_close_result() = runVmTest { dispatchers ->
        val printer = RecordingPrinter()
        val vm = newVm(dispatchers, printer = printer)
        advanceUntilIdle()

        vm.printReceipt()
        advanceUntilIdle()

        assertEquals(0, printer.calls)
        assertNull(vm.state.value.errorState)
    }

    @Test
    fun printReceipt_after_close_calls_printer_with_built_template() = runVmTest { dispatchers ->
        val printer = RecordingPrinter(result = true)
        val vm = newVm(dispatchers, printer = printer)
        advanceUntilIdle()
        vm.requestCloseDay()
        vm.confirmCloseDay()
        advanceUntilIdle()

        vm.printReceipt()
        advanceUntilIdle()

        assertEquals(1, printer.calls)
        val tpl = printer.lastTemplate
        assertNotNull(tpl)
        assertEquals("EOD-2026-05-19", tpl.billNo)
        assertEquals(sampleReport.totalSales, tpl.total)
        assertEquals(sampleReport.totalReceived, tpl.received)
        assertEquals(sampleReport.totalChange, tpl.change)
        assertEquals(sampleReport.totalDiscount, tpl.cartDiscount)
        assertNull(vm.state.value.errorState)
    }

    @Test
    fun printReceipt_surfaces_error_when_platform_not_supported() = runVmTest { dispatchers ->
        val printer = RecordingPrinter(result = false)
        val vm = newVm(dispatchers, printer = printer)
        advanceUntilIdle()
        vm.requestCloseDay()
        vm.confirmCloseDay()
        advanceUntilIdle()

        vm.printReceipt()
        advanceUntilIdle()

        assertEquals(1, printer.calls)
        assertIs<EodUiStateError.PrintReceiptUnsupported>(vm.state.value.errorState)
    }

    @Test
    fun cancelCloseDay_dismisses_confirm_without_calling_repo() = runVmTest { dispatchers ->
        val reports = FakeReportsRepository(eodResult = sampleReport)
        val vm = newVm(dispatchers, reports = reports)
        advanceUntilIdle()
        vm.requestCloseDay()
        assertTrue(vm.state.value.confirmClose)
        vm.cancelCloseDay()
        advanceUntilIdle()
        assertFalse(vm.state.value.confirmClose)
        assertEquals(0, reports.closeCallCount)
        assertFalse(vm.state.value.closed)
    }

    @Test
    fun onDateChange_clears_closed_and_closeResult() = runVmTest { dispatchers ->
        val vm = newVm(dispatchers)
        advanceUntilIdle()
        vm.requestCloseDay()
        vm.confirmCloseDay()
        advanceUntilIdle()
        assertTrue(vm.state.value.closed)
        assertNotNull(vm.state.value.closeResult)

        vm.onDateChange("2026-05-18")
        assertFalse(vm.state.value.closed)
        assertNull(vm.state.value.closeResult)
    }
}
