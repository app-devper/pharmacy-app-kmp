package app.devper.pharm.presentation.stockcount

import app.devper.pharm.common.AppDispatchers
import app.devper.pharm.domain.model.Drug
import app.devper.pharm.domain.repository.FakeDrugRepository
import app.devper.pharm.domain.repository.FakeStockCountsRepository
import app.devper.pharm.domain.usecase.CreateStockCountUseCase
import app.devper.pharm.domain.usecase.GetDrugsUseCase
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
class StockCountFormViewModelTest {

    private fun drug(id: String, name: String = "Drug $id") = Drug(
        id = id,
        name = name,
        genericName = null,
        type = null,
        strength = null,
        barcode = null,
        sellPrice = 5.0,
        costPrice = 2.0,
        stock = 100,
        minStock = 0,
        unit = "เม็ด",
        regNo = null,
    )

    private data class Bundle(
        val vm: StockCountFormViewModel,
        val drugs: FakeDrugRepository,
        val counts: FakeStockCountsRepository,
    )

    private fun newVm(
        dispatchers: AppDispatchers,
        drugs: FakeDrugRepository = FakeDrugRepository(),
        counts: FakeStockCountsRepository = FakeStockCountsRepository(),
    ): Bundle {
        val vm = StockCountFormViewModel(
            getDrugs = GetDrugsUseCase(drugs, dispatchers),
            createStockCount = CreateStockCountUseCase(counts, dispatchers),
        )
        return Bundle(vm, drugs, counts)
    }

    @Test
    fun init_loads_drug_list() = runVmTest { dispatchers ->
        val (vm, repo, _) = newVm(
            dispatchers,
            drugs = FakeDrugRepository(seed = listOf(drug("d1"), drug("d2"))),
        )
        advanceUntilIdle()
        val s = vm.state.value
        assertEquals(2, s.drugs.size)
        assertFalse(s.loading)
        assertEquals(1, repo.listCallCount)
    }

    @Test
    fun init_failure_surfaces_error() = runVmTest { dispatchers ->
        val (vm, _, _) = newVm(dispatchers, drugs = FakeDrugRepository(listThrows = true))
        advanceUntilIdle()
        assertNotNull(vm.state.value.error)
        assertFalse(vm.state.value.loading)
        assertTrue(vm.state.value.drugs.isEmpty())
    }

    @Test
    fun onQueryChange_filters_drugs_via_DrugSearch() = runVmTest { dispatchers ->
        val (vm, _, _) = newVm(
            dispatchers,
            drugs = FakeDrugRepository(seed = listOf(drug("d1", "Paracetamol"), drug("d2", "Ibuprofen"))),
        )
        advanceUntilIdle()
        vm.onQueryChange("Para")
        val filtered = vm.state.value.filtered
        assertEquals(1, filtered.size)
        assertEquals("d1", filtered[0].id)
    }

    @Test
    fun onCountChange_strips_non_digits_and_adds_entry() = runVmTest { dispatchers ->
        val (vm, _, _) = newVm(dispatchers, drugs = FakeDrugRepository(seed = listOf(drug("d1"))))
        advanceUntilIdle()
        vm.onCountChange("d1", "12abc.5")
        assertEquals("125", vm.state.value.counts["d1"])
    }

    @Test
    fun onCountChange_blank_removes_entry() = runVmTest { dispatchers ->
        val (vm, _, _) = newVm(dispatchers, drugs = FakeDrugRepository(seed = listOf(drug("d1"))))
        advanceUntilIdle()
        vm.onCountChange("d1", "5")
        assertEquals("5", vm.state.value.counts["d1"])
        vm.onCountChange("d1", "")
        assertFalse("d1" in vm.state.value.counts)
    }

    @Test
    fun canSubmit_requires_at_least_one_pending_line() = runVmTest { dispatchers ->
        val (vm, _, _) = newVm(dispatchers, drugs = FakeDrugRepository(seed = listOf(drug("d1"))))
        advanceUntilIdle()
        assertFalse(vm.state.value.canSubmit)
        vm.onCountChange("d1", "10")
        assertTrue(vm.state.value.canSubmit)
    }

    @Test
    fun pendingLines_aggregates_valid_counts_only() = runVmTest { dispatchers ->
        val (vm, _, _) = newVm(dispatchers, drugs = FakeDrugRepository(seed = listOf(drug("d1"), drug("d2"))))
        advanceUntilIdle()
        vm.onCountChange("d1", "5")
        vm.onCountChange("d2", "3")
        val pending = vm.state.value.pendingLines.toMap()
        assertEquals(5, pending["d1"])
        assertEquals(3, pending["d2"])
    }

    @Test
    fun submit_happy_path_sends_CreateStockCountParam_marks_saved() = runVmTest { dispatchers ->
        val (vm, _, counts) = newVm(dispatchers, drugs = FakeDrugRepository(seed = listOf(drug("d1"), drug("d2"))))
        advanceUntilIdle()
        vm.onNoteChange("Cycle May")
        vm.onCountChange("d1", "5")
        vm.onCountChange("d2", "3")
        vm.submit()
        advanceUntilIdle()
        val p = counts.lastAdd
        assertNotNull(p)
        assertEquals("Cycle May", p.note)
        assertEquals(2, p.items.size)
        val byId = p.items.associate { it.drugId to it.counted }
        assertEquals(5, byId["d1"])
        assertEquals(3, byId["d2"])
        assertTrue(vm.state.value.saved)
        assertFalse(vm.state.value.saving)
    }

    @Test
    fun submit_failure_surfaces_error() = runVmTest { dispatchers ->
        val (vm, _, _) = newVm(
            dispatchers,
            drugs = FakeDrugRepository(seed = listOf(drug("d1"))),
            counts = FakeStockCountsRepository(addThrows = true),
        )
        advanceUntilIdle()
        vm.onCountChange("d1", "5")
        vm.submit()
        advanceUntilIdle()
        assertFalse(vm.state.value.saved)
        assertNotNull(vm.state.value.error)
        assertFalse(vm.state.value.saving)
    }

    @Test
    fun dismissError_clears_error() = runVmTest { dispatchers ->
        val (vm, _, _) = newVm(dispatchers, drugs = FakeDrugRepository(listThrows = true))
        advanceUntilIdle()
        assertNotNull(vm.state.value.error)
        vm.dismissError()
        assertNull(vm.state.value.error)
    }
}
