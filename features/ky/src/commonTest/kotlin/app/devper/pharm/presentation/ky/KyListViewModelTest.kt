package app.devper.pharm.presentation.ky

import app.devper.pharm.domain.model.KyFormType
import app.devper.pharm.domain.repository.FakeExportRepository
import app.devper.pharm.domain.repository.FakeKyRepository
import app.devper.pharm.domain.usecase.ky.ExportKyFormUseCase
import app.devper.pharm.domain.usecase.ky.GetKy10EntriesUseCase
import app.devper.pharm.domain.usecase.ky.GetKy11EntriesUseCase
import app.devper.pharm.domain.usecase.ky.GetKy12EntriesUseCase
import app.devper.pharm.ui.common.runVmTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlin.test.Test
import app.devper.pharm.common.error.CommonUiStateMessage
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertNull

@OptIn(ExperimentalCoroutinesApi::class)
class KyListViewModelTest {

    private fun vm(export: FakeExportRepository = FakeExportRepository()): (d: app.devper.pharm.common.AppDispatchers) -> KyListViewModel =
        { d ->
            val repo = FakeKyRepository()
            KyListViewModel(
                GetKy10EntriesUseCase(repo, d),
                GetKy11EntriesUseCase(repo, d),
                GetKy12EntriesUseCase(repo, d),
                ExportKyFormUseCase(export, d),
            )
        }

    @Test
    fun init_sets_form_type_and_loads() = runVmTest { d ->
        val model = vm()(d)
        model.init(KyFormType.Ky10)
        advanceUntilIdle()
        assertEquals(KyFormType.Ky10, model.state.value.formType)
        assertFalse(model.state.value.loading)
        assertNull(model.state.value.errorState)
    }

    @Test
    fun export_pdf_sets_message() = runVmTest { d ->
        val export = FakeExportRepository(result = "ดาวน์โหลดแล้ว")
        val model = vm(export)(d)
        model.init(KyFormType.Ky11)
        advanceUntilIdle()
        model.exportPdf()
        advanceUntilIdle()
        val msg = assertIs<CommonUiStateMessage.ExportDone>(model.state.value.messageState)
        assertEquals("ดาวน์โหลดแล้ว", msg.path)
        assertNotNull(export.lastKyParam)
        assertEquals("ky11", export.lastKyParam?.form)
    }
}
