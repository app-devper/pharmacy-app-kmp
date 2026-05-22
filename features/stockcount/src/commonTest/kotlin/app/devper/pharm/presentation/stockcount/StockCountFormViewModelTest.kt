package app.devper.pharm.presentation.stockcount

import app.devper.pharm.common.AppDispatchers
import app.devper.pharm.domain.model.Drug
import app.devper.pharm.domain.model.StockCountDraft
import app.devper.pharm.domain.repository.FakeDrugRepository
import app.devper.pharm.domain.repository.FakeStockCountDraftRepository
import app.devper.pharm.domain.repository.FakeStockCountsRepository
import app.devper.pharm.domain.usecase.CreateStockCountUseCase
import app.devper.pharm.domain.usecase.GetDrugsUseCase
import app.devper.pharm.ui.common.runVmTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class StockCountFormViewModelTest {

    private fun drug(id: String, name: String = "Drug $id", stock: Int = 100) = Drug(
        id = id,
        name = name,
        genericName = null,
        type = null,
        strength = null,
        barcode = null,
        sellPrice = 5.0,
        costPrice = 2.0,
        stock = stock,
        minStock = 0,
        unit = "เม็ด",
        regNo = null,
    )

    private data class Bundle(
        val vm: StockCountFormViewModel,
        val drugs: FakeDrugRepository,
        val counts: FakeStockCountsRepository,
        val drafts: FakeStockCountDraftRepository,
    )

    private fun newVm(
        dispatchers: AppDispatchers,
        drugs: FakeDrugRepository = FakeDrugRepository(),
        counts: FakeStockCountsRepository = FakeStockCountsRepository(),
        drafts: FakeStockCountDraftRepository = FakeStockCountDraftRepository(),
    ): Bundle {
        val vm = StockCountFormViewModel(
            getDrugs = GetDrugsUseCase(drugs, dispatchers),
            createStockCount = CreateStockCountUseCase(counts, dispatchers),
            draftRepo = drafts,
        )
        return Bundle(vm, drugs, counts, drafts)
    }

    @Test
    fun init_loads_drug_list() = runVmTest { dispatchers ->
        val (vm, repo, _, _) = newVm(
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
        val (vm, _, _, _) = newVm(dispatchers, drugs = FakeDrugRepository(listThrows = true))
        advanceUntilIdle()
        assertNotNull(vm.state.value.error)
        assertFalse(vm.state.value.loading)
        assertTrue(vm.state.value.drugs.isEmpty())
    }

    @Test
    fun onQueryChange_filters_drugs_via_DrugSearch() = runVmTest { dispatchers ->
        val (vm, _, _, _) = newVm(
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
        val (vm, _, _, _) = newVm(dispatchers, drugs = FakeDrugRepository(seed = listOf(drug("d1"))))
        advanceUntilIdle()
        vm.onCountChange("d1", "12abc.5")
        assertEquals("125", vm.state.value.counts["d1"])
    }

    @Test
    fun onCountChange_blank_removes_entry() = runVmTest { dispatchers ->
        val (vm, _, _, _) = newVm(dispatchers, drugs = FakeDrugRepository(seed = listOf(drug("d1"))))
        advanceUntilIdle()
        vm.onCountChange("d1", "5")
        assertEquals("5", vm.state.value.counts["d1"])
        vm.onCountChange("d1", "")
        assertFalse("d1" in vm.state.value.counts)
    }

    @Test
    fun canSubmit_requires_at_least_one_pending_line() = runVmTest { dispatchers ->
        val (vm, _, _, _) = newVm(dispatchers, drugs = FakeDrugRepository(seed = listOf(drug("d1"))))
        advanceUntilIdle()
        assertFalse(vm.state.value.canSubmit)
        vm.onCountChange("d1", "10")
        assertTrue(vm.state.value.canSubmit)
    }

    @Test
    fun pendingLines_aggregates_valid_counts_only() = runVmTest { dispatchers ->
        val (vm, _, _, _) = newVm(dispatchers, drugs = FakeDrugRepository(seed = listOf(drug("d1"), drug("d2"))))
        advanceUntilIdle()
        vm.onCountChange("d1", "5")
        vm.onCountChange("d2", "3")
        val pending = vm.state.value.pendingLines.toMap()
        assertEquals(5, pending["d1"])
        assertEquals(3, pending["d2"])
    }

    @Test
    fun submit_happy_path_sends_CreateStockCountParam_marks_saved() = runVmTest { dispatchers ->
        val (vm, _, counts, _) = newVm(dispatchers, drugs = FakeDrugRepository(seed = listOf(drug("d1"), drug("d2"))))
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
        val (vm, _, _, _) = newVm(
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
        val (vm, _, _, _) = newVm(dispatchers, drugs = FakeDrugRepository(listThrows = true))
        advanceUntilIdle()
        assertNotNull(vm.state.value.error)
        vm.dismissError()
        assertNull(vm.state.value.error)
    }

    @Test
    fun init_hydrates_counts_and_note_from_non_empty_draft() = runVmTest { dispatchers ->
        val drafts = FakeStockCountDraftRepository(
            initial = StockCountDraft(
                counts = mapOf("d1" to "7", "d2" to "9"),
                note = "ก่อนปิดเดือน",
                updatedAt = 1L,
            )
        )
        val (vm, _, _, _) = newVm(
            dispatchers,
            drugs = FakeDrugRepository(seed = listOf(drug("d1"), drug("d2"))),
            drafts = drafts,
        )
        advanceUntilIdle()
        val s = vm.state.value
        assertEquals("7", s.counts["d1"])
        assertEquals("9", s.counts["d2"])
        assertEquals("ก่อนปิดเดือน", s.note)
    }

    @Test
    fun count_change_persists_to_draft_after_debounce() = runVmTest { dispatchers ->
        val drafts = FakeStockCountDraftRepository()
        val (vm, _, _, _) = newVm(
            dispatchers,
            drugs = FakeDrugRepository(seed = listOf(drug("d1"))),
            drafts = drafts,
        )
        advanceUntilIdle()
        val savesBefore = drafts.saveCallCount
        vm.onCountChange("d1", "42")
        advanceTimeBy(100)
        assertEquals(savesBefore, drafts.saveCallCount)
        advanceTimeBy(600)
        advanceUntilIdle()
        assertTrue(drafts.saveCallCount > savesBefore)
        assertEquals("42", drafts.stored.counts["d1"])
    }

    @Test
    fun note_change_persists_to_draft_after_debounce() = runVmTest { dispatchers ->
        val drafts = FakeStockCountDraftRepository()
        val (vm, _, _, _) = newVm(
            dispatchers,
            drugs = FakeDrugRepository(seed = listOf(drug("d1"))),
            drafts = drafts,
        )
        advanceUntilIdle()
        val savesBefore = drafts.saveCallCount
        vm.onNoteChange("ก่อนตรวจ")
        advanceTimeBy(600)
        advanceUntilIdle()
        assertTrue(drafts.saveCallCount > savesBefore)
        assertEquals("ก่อนตรวจ", drafts.stored.note)
    }

    @Test
    fun successful_submit_clears_draft() = runVmTest { dispatchers ->
        val drafts = FakeStockCountDraftRepository()
        val (vm, _, _, _) = newVm(
            dispatchers,
            drugs = FakeDrugRepository(seed = listOf(drug("d1"))),
            drafts = drafts,
        )
        advanceUntilIdle()
        vm.onCountChange("d1", "5")
        advanceTimeBy(600)
        advanceUntilIdle()
        assertTrue(drafts.stored.counts.isNotEmpty())
        vm.submit()
        advanceUntilIdle()
        assertTrue(vm.state.value.saved)
        assertTrue(drafts.clearCallCount >= 1)
        assertTrue(drafts.stored.isEmpty)
    }

    @Test
    fun onClearDraft_resets_counts_and_note() = runVmTest { dispatchers ->
        val (vm, _, _, drafts) = newVm(
            dispatchers,
            drugs = FakeDrugRepository(seed = listOf(drug("d1"))),
        )
        advanceUntilIdle()
        vm.onCountChange("d1", "5")
        vm.onNoteChange("note")
        advanceTimeBy(600)
        advanceUntilIdle()
        assertTrue(drafts.stored.counts.isNotEmpty())
        vm.onClearDraft()
        advanceTimeBy(600)
        advanceUntilIdle()
        assertTrue(vm.state.value.counts.isEmpty())
        assertEquals("", vm.state.value.note)
        assertTrue(drafts.stored.isEmpty)
    }

    @Test
    fun requestSubmit_flips_showSubmitConfirm_when_canSubmit() = runVmTest { dispatchers ->
        val (vm, _, _, _) = newVm(dispatchers, drugs = FakeDrugRepository(seed = listOf(drug("d1"))))
        advanceUntilIdle()
        assertFalse(vm.state.value.showSubmitConfirm)
        vm.requestSubmit()
        assertFalse(vm.state.value.showSubmitConfirm)
        vm.onCountChange("d1", "5")
        vm.requestSubmit()
        assertTrue(vm.state.value.showSubmitConfirm)
    }

    @Test
    fun cancelSubmit_clears_flag_without_firing_use_case() = runVmTest { dispatchers ->
        val (vm, _, counts, _) = newVm(dispatchers, drugs = FakeDrugRepository(seed = listOf(drug("d1"))))
        advanceUntilIdle()
        vm.onCountChange("d1", "5")
        vm.requestSubmit()
        assertTrue(vm.state.value.showSubmitConfirm)
        vm.cancelSubmit()
        advanceUntilIdle()
        assertFalse(vm.state.value.showSubmitConfirm)
        assertNull(counts.lastAdd)
        assertFalse(vm.state.value.saved)
    }

    @Test
    fun confirmSubmit_fires_use_case_and_clears_flag() = runVmTest { dispatchers ->
        val (vm, _, counts, _) = newVm(dispatchers, drugs = FakeDrugRepository(seed = listOf(drug("d1"))))
        advanceUntilIdle()
        vm.onCountChange("d1", "5")
        vm.requestSubmit()
        vm.confirmSubmit()
        advanceUntilIdle()
        assertFalse(vm.state.value.showSubmitConfirm)
        assertNotNull(counts.lastAdd)
        assertTrue(vm.state.value.saved)
    }

    @Test
    fun orphan_draft_entries_are_dropped_when_drugs_load() = runVmTest { dispatchers ->
        val drafts = FakeStockCountDraftRepository(
            initial = StockCountDraft(
                counts = mapOf("d1" to "5", "ghost" to "9"),
                note = "n",
                updatedAt = 1L,
            )
        )
        val (vm, _, _, _) = newVm(
            dispatchers,
            drugs = FakeDrugRepository(seed = listOf(drug("d1"))),
            drafts = drafts,
        )
        advanceUntilIdle()
        val s = vm.state.value
        assertEquals(setOf("d1"), s.counts.keys)
        assertEquals("5", s.counts["d1"])
    }

    @Test
    fun topDiscrepancies_orders_by_abs_delta_and_limits_to_five() = runVmTest { dispatchers ->
        val (vm, _, _, _) = newVm(
            dispatchers,
            drugs = FakeDrugRepository(
                seed = listOf(
                    drug("a", stock = 10),
                    drug("b", stock = 100),
                    drug("c", stock = 50),
                    drug("d", stock = 0),
                    drug("e", stock = 20),
                    drug("f", stock = 1),
                ),
            ),
        )
        advanceUntilIdle()
        vm.onCountChange("a", "12")
        vm.onCountChange("b", "60")
        vm.onCountChange("c", "55")
        vm.onCountChange("d", "7")
        vm.onCountChange("e", "21")
        vm.onCountChange("f", "31")
        val tops = vm.state.value.topDiscrepancies
        assertEquals(5, tops.size)
        assertEquals(listOf("b", "f", "d", "c", "a"), tops.map { it.drugId })
    }
}
