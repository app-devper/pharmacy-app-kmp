package app.devper.pharm.presentation.expiry

import app.devper.pharm.domain.model.ExpiringLot
import app.devper.pharm.domain.model.WriteoffResult
import app.devper.pharm.domain.repository.FakeExpiringLotsRepository
import app.devper.pharm.domain.usecase.inventory.GetExpiringLotsUseCase
import app.devper.pharm.domain.usecase.inventory.WriteoffLotsUseCase
import app.devper.pharm.ui.common.runVmTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
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
}
