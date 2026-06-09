package app.devper.pharm.presentation.stock

import app.devper.pharm.common.AppDispatchers
import app.devper.pharm.domain.model.AdjustmentReason
import app.devper.pharm.domain.model.StockAdjustment
import app.devper.pharm.domain.repository.FakeStockAdjustmentsRepository
import app.devper.pharm.domain.usecase.AddStockAdjustmentUseCase
import app.devper.pharm.domain.usecase.GetStockAdjustmentsUseCase
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
class StockAdjustmentsViewModelTest {

    private fun adjustment(id: String = "a1", delta: Int = -5) = StockAdjustment(
        id = id,
        drugId = "d1",
        drugName = "Paracetamol",
        delta = delta,
        before = 100,
        after = 100 + delta,
        reason = AdjustmentReason.Recount,
        note = "",
        at = "2026-05-14T10:00:00Z",
    )

    private data class Bundle(
        val vm: StockAdjustmentsViewModel,
        val repo: FakeStockAdjustmentsRepository,
    )

    private fun newVm(
        dispatchers: AppDispatchers,
        repo: FakeStockAdjustmentsRepository = FakeStockAdjustmentsRepository(),
    ): Bundle {
        val vm = StockAdjustmentsViewModel(
            getAdjustments = GetStockAdjustmentsUseCase(repo, dispatchers),
            addAdjustment = AddStockAdjustmentUseCase(repo, dispatchers),
        )
        return Bundle(vm, repo)
    }

    @Test
    fun open_loads_history_for_drug() = runVmTest { dispatchers ->
        val (vm, repo) = newVm(
            dispatchers,
            FakeStockAdjustmentsRepository(
                seed = mapOf("d1" to listOf(adjustment("a1"), adjustment("a2", delta = 3))),
            ),
        )
        advanceUntilIdle()
        vm.open("d1", "Paracetamol")
        advanceUntilIdle()
        val s = vm.state.value
        assertEquals("d1", s.drugId)
        assertEquals("Paracetamol", s.drugName)
        assertEquals(2, s.history.size)
        assertFalse(s.loading)
        assertEquals(1, repo.listCallCount)
    }

    @Test
    fun open_resets_form_and_error() = runVmTest { dispatchers ->
        val (vm, _) = newVm(dispatchers)
        advanceUntilIdle()
        vm.open("d1", "Paracetamol")
        vm.toggleAddForm()
        vm.onAbsDelta("5")
        vm.open("d2", "Ibuprofen")
        advanceUntilIdle()
        assertFalse(vm.state.value.addFormOpen)
        assertEquals("", vm.state.value.draft.absDelta)
    }

    @Test
    fun close_resets_to_initial_state() = runVmTest { dispatchers ->
        val (vm, _) = newVm(dispatchers)
        advanceUntilIdle()
        vm.open("d1", "Paracetamol")
        advanceUntilIdle()
        vm.close()
        val s = vm.state.value
        assertEquals("", s.drugId)
        assertEquals("", s.drugName)
        assertTrue(s.history.isEmpty())
    }

    @Test
    fun reload_no_op_when_drugId_blank() = runVmTest { dispatchers ->
        val (vm, repo) = newVm(dispatchers)
        advanceUntilIdle()
        vm.reload()
        advanceUntilIdle()
        assertEquals(0, repo.listCallCount)
    }

    @Test
    fun toggleAddForm_flips_flag_and_clears_draft() = runVmTest { dispatchers ->
        val (vm, _) = newVm(dispatchers)
        advanceUntilIdle()
        vm.open("d1", "Paracetamol")
        advanceUntilIdle()
        assertFalse(vm.state.value.addFormOpen)
        vm.toggleAddForm()
        assertTrue(vm.state.value.addFormOpen)
        vm.onAbsDelta("7")
        vm.toggleAddForm()
        assertFalse(vm.state.value.addFormOpen)
        assertEquals("", vm.state.value.draft.absDelta)
    }

    @Test
    fun onAbsDelta_strips_non_digit_chars() = runVmTest { dispatchers ->
        val (vm, _) = newVm(dispatchers)
        advanceUntilIdle()
        vm.onAbsDelta("12.5abc")
        assertEquals("125", vm.state.value.draft.absDelta)
    }

    @Test
    fun signedDelta_decrease_negates_abs() = runVmTest { dispatchers ->
        val (vm, _) = newVm(dispatchers)
        advanceUntilIdle()
        vm.onSign(AdjustmentSign.Decrease)
        vm.onAbsDelta("3")
        assertEquals(-3, vm.state.value.signedDelta())
        vm.onSign(AdjustmentSign.Increase)
        assertEquals(3, vm.state.value.signedDelta())
    }

    @Test
    fun canSubmitDraft_false_when_delta_zero_or_blank() = runVmTest { dispatchers ->
        val (vm, _) = newVm(dispatchers)
        advanceUntilIdle()
        assertFalse(vm.state.value.canSubmitDraft)
        vm.onAbsDelta("0")
        assertFalse(vm.state.value.canSubmitDraft)
        vm.onAbsDelta("4")
        assertTrue(vm.state.value.canSubmitDraft)
    }

    @Test
    fun submitAdd_happy_path_persists_and_reloads() = runVmTest { dispatchers ->
        val (vm, repo) = newVm(dispatchers)
        advanceUntilIdle()
        vm.open("d1", "Paracetamol")
        advanceUntilIdle()
        val listCallsBefore = repo.listCallCount
        vm.toggleAddForm()
        vm.onSign(AdjustmentSign.Decrease)
        vm.onAbsDelta("4")
        vm.onReason(AdjustmentReason.Damaged)
        vm.onNote("ตก")
        vm.submitAdd()
        advanceUntilIdle()
        val captured = repo.lastAdd
        assertNotNull(captured)
        assertEquals("d1", captured.drugId)
        assertEquals(-4, captured.delta)
        assertEquals(AdjustmentReason.Damaged, captured.reason)
        assertEquals("ตก", captured.note)
        assertFalse(vm.state.value.saving)
        assertFalse(vm.state.value.addFormOpen)
        assertTrue(repo.listCallCount > listCallsBefore)
    }

    @Test
    fun submitAdd_no_op_when_delta_zero() = runVmTest { dispatchers ->
        val (vm, repo) = newVm(dispatchers)
        advanceUntilIdle()
        vm.open("d1", "Paracetamol")
        advanceUntilIdle()
        vm.toggleAddForm()
        vm.submitAdd()
        advanceUntilIdle()
        assertNull(repo.lastAdd)
        assertFalse(vm.state.value.saving)
    }

    @Test
    fun submitAdd_failure_keeps_form_open_and_surfaces_error() = runVmTest { dispatchers ->
        val (vm, _) = newVm(
            dispatchers,
            FakeStockAdjustmentsRepository(addThrowsOn = "d1"),
        )
        advanceUntilIdle()
        vm.open("d1", "Paracetamol")
        advanceUntilIdle()
        vm.toggleAddForm()
        vm.onAbsDelta("5")
        vm.submitAdd()
        advanceUntilIdle()
        assertNotNull(vm.state.value.errorState)
        assertFalse(vm.state.value.saving)
        assertTrue(vm.state.value.addFormOpen)
    }

    @Test
    fun dismissError_clears_error() = runVmTest { dispatchers ->
        val (vm, _) = newVm(dispatchers, FakeStockAdjustmentsRepository(listThrows = true))
        advanceUntilIdle()
        vm.open("d1", "Paracetamol")
        advanceUntilIdle()
        assertNotNull(vm.state.value.errorState)
        vm.dismissError()
        assertNull(vm.state.value.errorState)
    }
}
