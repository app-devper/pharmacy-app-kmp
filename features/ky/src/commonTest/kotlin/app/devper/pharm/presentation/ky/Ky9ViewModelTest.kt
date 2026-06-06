package app.devper.pharm.presentation.ky

import app.devper.pharm.domain.repository.FakeExportRepository
import app.devper.pharm.domain.repository.FakeKyRepository
import app.devper.pharm.domain.usecase.ExportKyFormUseCase
import app.devper.pharm.domain.usecase.GetKy9EntriesUseCase
import app.devper.pharm.ui.common.runVmTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull

@OptIn(ExperimentalCoroutinesApi::class)
class Ky9ViewModelTest {

    @Test
    fun init_loads_without_error() = runVmTest { d ->
        val repo = FakeKyRepository()
        val vm = Ky9ViewModel(GetKy9EntriesUseCase(repo, d), ExportKyFormUseCase(FakeExportRepository(), d))
        advanceUntilIdle()
        assertFalse(vm.state.value.loading)
        assertNull(vm.state.value.error)
    }

    @Test
    fun month_change_updates_state() = runVmTest { d ->
        val repo = FakeKyRepository()
        val vm = Ky9ViewModel(GetKy9EntriesUseCase(repo, d), ExportKyFormUseCase(FakeExportRepository(), d))
        advanceUntilIdle()
        vm.onMonthChange("2026-05")
        assertEquals("2026-05", vm.state.value.month)
    }

    @Test
    fun export_pdf_uses_ky9_form() = runVmTest { d ->
        val export = FakeExportRepository(result = "saved.pdf")
        val vm = Ky9ViewModel(GetKy9EntriesUseCase(FakeKyRepository(), d), ExportKyFormUseCase(export, d))
        advanceUntilIdle()
        vm.exportPdf()
        advanceUntilIdle()
        assertEquals("saved.pdf", vm.state.value.message)
        assertEquals("ky9", export.lastKyParam?.form)
    }
}
