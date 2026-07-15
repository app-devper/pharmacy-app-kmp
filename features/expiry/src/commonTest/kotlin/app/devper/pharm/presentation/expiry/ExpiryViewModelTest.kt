package app.devper.pharm.presentation.expiry

import app.devper.pharm.domain.model.ExpiringLot
import app.devper.pharm.domain.model.WriteoffResult
import app.devper.pharm.domain.repository.FakeExpiringLotsRepository
import app.devper.pharm.domain.usecase.inventory.GetExpiringLotsUseCase
import app.devper.pharm.domain.usecase.inventory.WriteoffLotsUseCase
import app.devper.pharm.ui.common.runVmTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import app.devper.pharm.common.AppException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class ExpiryViewModelTest {

    private fun lot(id: String) = ExpiringLot(
        id = id, drugId = "drug-$id", drugName = "Drug $id",
        lotNumber = "L$id", expiryDate = kotlinx.datetime.LocalDate.parse("2026-07-01"), remaining = 10, daysLeft = 25,
    )

    @Test
    fun init_loads_lots_for_default_window() = runVmTest { d ->
        val repo = FakeExpiringLotsRepository(seed = listOf(lot("a"), lot("b")))
        val vm = ExpiryViewModel(GetExpiringLotsUseCase(repo, d), WriteoffLotsUseCase(repo, d))
        advanceUntilIdle()
        assertEquals(2, vm.state.value.lots.size)
        assertFalse(vm.state.value.loading)
    }

    @Test
    fun select_window_reloads_with_new_filter() = runVmTest { d ->
        val repo = FakeExpiringLotsRepository(seed = listOf(lot("a")))
        val vm = ExpiryViewModel(GetExpiringLotsUseCase(repo, d), WriteoffLotsUseCase(repo, d))
        advanceUntilIdle()
        vm.selectWindow(ExpiryWindow.ExpiredOnly)
        advanceUntilIdle()
        assertEquals(true, repo.lastFilter?.expiredOnly)
        assertEquals(ExpiryWindow.ExpiredOnly, vm.state.value.window)
    }

    @Test
    fun confirm_writeoff_clears_selection_and_sets_result() = runVmTest { d ->
        val repo = FakeExpiringLotsRepository(
            seed = listOf(lot("a"), lot("b")),
            writeoffResult = WriteoffResult(writtenOff = 1, failures = emptyList()),
        )
        val vm = ExpiryViewModel(GetExpiringLotsUseCase(repo, d), WriteoffLotsUseCase(repo, d))
        advanceUntilIdle()
        vm.toggleSelected("a")
        vm.confirmWriteoff()
        advanceUntilIdle()
        assertEquals(listOf("a"), repo.lastWriteoff?.lotIds)
        assertTrue(vm.state.value.selected.isEmpty())
        assertEquals(1, vm.state.value.writeoffResult?.writtenOff)
    }

    @Test
    fun toggle_selected_adds_then_removes_lot() = runVmTest { d ->
        val repo = FakeExpiringLotsRepository(seed = listOf(lot("x")))
        val vm = ExpiryViewModel(GetExpiringLotsUseCase(repo, d), WriteoffLotsUseCase(repo, d))
        advanceUntilIdle()
        vm.toggleSelected("x")
        assertTrue("x" in vm.state.value.selected)
        vm.toggleSelected("x")
        assertFalse("x" in vm.state.value.selected)
    }

    @Test
    fun ask_confirm_and_cancel_toggles_dialog() = runVmTest { d ->
        val repo = FakeExpiringLotsRepository(seed = listOf(lot("a")))
        val vm = ExpiryViewModel(GetExpiringLotsUseCase(repo, d), WriteoffLotsUseCase(repo, d))
        advanceUntilIdle()
        vm.toggleSelected("a")
        vm.askConfirm()
        assertTrue(vm.state.value.confirmDialog)
        vm.cancelConfirm()
        assertFalse(vm.state.value.confirmDialog)
    }

    @Test
    fun list_failure_sets_error_state() = runVmTest { d ->
        val repo = FakeExpiringLotsRepository(listThrows = true)
        val vm = ExpiryViewModel(GetExpiringLotsUseCase(repo, d), WriteoffLotsUseCase(repo, d))
        advanceUntilIdle()
        assertNotNull(vm.state.value.errorState)
        assertFalse(vm.state.value.loading)
    }

    @Test
    fun writeoff_failure_sets_error_state() = runVmTest { d ->
        val repo = FakeExpiringLotsRepository(
            seed = listOf(lot("a")),
            writeoffThrows = true,
        )
        val vm = ExpiryViewModel(GetExpiringLotsUseCase(repo, d), WriteoffLotsUseCase(repo, d))
        advanceUntilIdle()
        vm.toggleSelected("a")
        vm.confirmWriteoff()
        advanceUntilIdle()
        assertNotNull(vm.state.value.errorState)
        assertNull(vm.state.value.writeoffResult)
    }

    @Test
    fun dismiss_result_clears_writeoff_result() = runVmTest { d ->
        val repo = FakeExpiringLotsRepository(
            seed = listOf(lot("a")),
            writeoffResult = WriteoffResult(writtenOff = 1, failures = emptyList()),
        )
        val vm = ExpiryViewModel(GetExpiringLotsUseCase(repo, d), WriteoffLotsUseCase(repo, d))
        advanceUntilIdle()
        vm.toggleSelected("a")
        vm.confirmWriteoff()
        advanceUntilIdle()
        assertNotNull(vm.state.value.writeoffResult)
        vm.dismissResult()
        assertNull(vm.state.value.writeoffResult)
    }

    @Test
    fun query_filters_by_drug_or_lot_and_select_all_affects_visible_rows_only() = runVmTest { d ->
        val repo = FakeExpiringLotsRepository(seed = listOf(lot("a"), lot("b"), lot("c")))
        val vm = ExpiryViewModel(GetExpiringLotsUseCase(repo, d), WriteoffLotsUseCase(repo, d))
        advanceUntilIdle()

        vm.onQueryChange("Lb")
        assertEquals(listOf("b"), vm.state.value.filteredLots.map { it.id })
        vm.toggleAll()

        assertEquals(setOf("b"), vm.state.value.selected)
        assertTrue(vm.state.value.allVisibleSelected)
    }

    @Test
    fun confirm_cannot_open_without_a_selection() = runVmTest { d ->
        val repo = FakeExpiringLotsRepository(seed = listOf(lot("a")))
        val vm = ExpiryViewModel(GetExpiringLotsUseCase(repo, d), WriteoffLotsUseCase(repo, d))
        advanceUntilIdle()

        vm.askConfirm()

        assertFalse(vm.state.value.confirmDialog)
    }
}
