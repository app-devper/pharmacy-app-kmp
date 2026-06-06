package app.devper.pharm.presentation.bulkimport

import app.devper.pharm.common.AppDispatchers
import app.devper.pharm.common.platform.FilePicker
import app.devper.pharm.domain.model.BulkImportResult
import app.devper.pharm.domain.repository.FakeDrugRepositoryForBulk
import app.devper.pharm.domain.usecase.BulkImportDrugsUseCase
import app.devper.pharm.ui.common.runVmTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class BulkImportViewModelTest {

    private object NoopFilePicker : FilePicker {
        override suspend fun pickJsonFile(): Result<String?> = Result.success(null)
    }

    private fun newVm(
        dispatchers: AppDispatchers,
        repo: FakeDrugRepositoryForBulk = FakeDrugRepositoryForBulk(),
        filePicker: FilePicker = NoopFilePicker,
    ): Pair<BulkImportViewModel, FakeDrugRepositoryForBulk> {
        val vm = BulkImportViewModel(
            bulkImportDrugs = BulkImportDrugsUseCase(repo, dispatchers),
            filePicker = filePicker,
        )
        return vm to repo
    }

    @Test
    fun preview_accepts_bare_array() = runVmTest { dispatchers ->
        val (vm, _) = newVm(dispatchers)
        vm.onTextChange("""[{"name": "A", "sell_price": 5}]""")
        vm.preview()
        val s = vm.state.value
        assertEquals(1, s.previewCount)
        assertNull(s.parseError)
    }

    @Test
    fun preview_accepts_drugs_object_wrapper() = runVmTest { dispatchers ->
        val (vm, _) = newVm(dispatchers)
        vm.onTextChange("""{"drugs": [{"name": "A", "sell_price": 5}, {"name": "B", "sell_price": 10}]}""")
        vm.preview()
        assertEquals(2, vm.state.value.previewCount)
    }

    @Test
    fun preview_rejects_blank_input() = runVmTest { dispatchers ->
        val (vm, _) = newVm(dispatchers)
        vm.onTextChange("   ")
        vm.preview()
        assertNotNull(vm.state.value.parseError)
        assertNull(vm.state.value.previewCount)
    }

    @Test
    fun preview_rejects_non_array_non_object() = runVmTest { dispatchers ->
        val (vm, _) = newVm(dispatchers)
        vm.onTextChange(""" "just a string" """)
        vm.preview()
        assertNotNull(vm.state.value.parseError)
    }

    @Test
    fun preview_rejects_row_without_name() = runVmTest { dispatchers ->
        val (vm, _) = newVm(dispatchers)
        vm.onTextChange("""[{"sell_price": 5}]""")
        vm.preview()
        assertNotNull(vm.state.value.parseError)
    }

    @Test
    fun submit_passes_typed_AddDrugParams_to_the_use_case() = runVmTest { dispatchers ->
        val (vm, repo) = newVm(dispatchers)
        vm.onTextChange(
            """[
                {"name": "Paracetamol", "sell_price": 2, "cost_price": 1, "stock": 5, "min_stock": 3, "unit": "เม็ด"},
                {"name": "Ibuprofen", "sell_price": 4}
            ]""".trimIndent()
        )
        vm.submit()
        advanceUntilIdle()

        val params = repo.lastBulkImport
        assertNotNull(params)
        assertEquals(2, params.size)
        assertEquals("Paracetamol", params[0].name)
        assertEquals(2.0, params[0].sellPrice)
        assertEquals(1.0, params[0].costPrice)
        assertEquals(5, params[0].stock)
        assertEquals(3, params[0].minStock)
        assertEquals("เม็ด", params[0].unit)
        assertEquals("Ibuprofen", params[1].name)

        assertEquals("ชิ้น", params[1].unit)
        assertEquals(0, params[1].stock)
    }

    @Test
    fun submit_surfaces_partial_success_result() = runVmTest { dispatchers ->
        val partial = BulkImportResult(imported = 1, errors = emptyList())
        val (vm, _) = newVm(dispatchers, FakeDrugRepositoryForBulk(result = partial))
        vm.onTextChange("""[{"name": "A", "sell_price": 1}]""")
        vm.submit()
        advanceUntilIdle()
        val s = vm.state.value
        assertEquals(partial, s.result)
        assertTrue(!s.submitting)
    }

    @Test
    fun submit_short_circuits_on_parse_error() = runVmTest { dispatchers ->
        val (vm, repo) = newVm(dispatchers)
        vm.onTextChange("not json")
        vm.submit()
        advanceUntilIdle()

        assertNull(repo.lastBulkImport)
        assertNotNull(vm.state.value.parseError)
    }
}
