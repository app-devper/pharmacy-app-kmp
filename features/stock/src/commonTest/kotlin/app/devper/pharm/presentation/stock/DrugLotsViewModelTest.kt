package app.devper.pharm.presentation.stock

import app.devper.pharm.common.value.Money
import app.devper.pharm.common.value.Quantity

import app.devper.pharm.common.AppDispatchers
import app.devper.pharm.domain.model.DrugLot
import app.devper.pharm.domain.repository.FakeLotsRepository
import app.devper.pharm.domain.usecase.AddLotUseCase
import app.devper.pharm.domain.usecase.DeleteLotUseCase
import app.devper.pharm.domain.usecase.ListLotsUseCase
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
class DrugLotsViewModelTest {

    private fun newVm(
        dispatchers: AppDispatchers,
        repo: FakeLotsRepository = FakeLotsRepository(),
    ): Pair<DrugLotsViewModel, FakeLotsRepository> {
        val vm = DrugLotsViewModel(
            listLots = ListLotsUseCase(repo, dispatchers),
            addLot = AddLotUseCase(repo, dispatchers),
            deleteLot = DeleteLotUseCase(repo, dispatchers),
        )
        return vm to repo
    }

    private fun lot(id: String, drugId: String, qty: Int = 100, expiry: String = "2026-12-31") = DrugLot(
        id = id,
        drugId = drugId,
        lotNumber = "L-$id",
        expiryDate = kotlinx.datetime.LocalDate.parse(expiry),
        importDate = kotlinx.datetime.LocalDate.parse("2026-01-01"),
        quantity = Quantity(qty),
        remaining = Quantity(qty),
    )

    @Test
    fun open_sets_drug_state_and_loads_lots() = runVmTest { dispatchers ->
        val (vm, _) = newVm(
            dispatchers,
            FakeLotsRepository(seed = listOf(lot("a", "d1"), lot("b", "d1"))),
        )
        vm.open(drugId = "d1", drugName = "Paracetamol")
        advanceUntilIdle()
        val s = vm.state.value
        assertEquals("d1", s.drugId)
        assertEquals("Paracetamol", s.drugName)
        assertEquals(2, s.lots.size)
        assertFalse(s.loading)
        assertNull(s.errorState)
    }

    @Test
    fun open_filters_lots_by_drugId() = runVmTest { dispatchers ->
        val (vm, _) = newVm(
            dispatchers,
            FakeLotsRepository(
                seed = listOf(lot("a", "d1"), lot("b", "d2"), lot("c", "d1")),
            ),
        )
        vm.open(drugId = "d1", drugName = "X")
        advanceUntilIdle()
        val ids = vm.state.value.lots.map { it.id }
        assertEquals(listOf("a", "c"), ids)
    }

    @Test
    fun reload_failure_surfaces_error() = runVmTest { dispatchers ->
        val (vm, _) = newVm(dispatchers, FakeLotsRepository(listThrowsOn = "d1"))
        vm.open(drugId = "d1", drugName = "X")
        advanceUntilIdle()
        val s = vm.state.value
        assertNotNull(s.errorState)
        assertFalse(s.loading)
        assertTrue(s.lots.isEmpty())
    }

    @Test
    fun close_wipes_state_to_default() = runVmTest { dispatchers ->
        val (vm, _) = newVm(
            dispatchers,
            FakeLotsRepository(seed = listOf(lot("a", "d1"))),
        )
        vm.open(drugId = "d1", drugName = "X")
        advanceUntilIdle()
        assertEquals("d1", vm.state.value.drugId)
        vm.close()
        val s = vm.state.value
        assertEquals("", s.drugId)
        assertEquals("", s.drugName)
        assertTrue(s.lots.isEmpty())
        assertFalse(s.addFormOpen)
        assertNull(s.pendingDelete)
    }

    @Test
    fun canSubmitDraft_requires_lotNumber_expiryDate_and_positive_quantity() = runVmTest { dispatchers ->
        val (vm, _) = newVm(dispatchers)
        vm.open(drugId = "d1", drugName = "X")
        advanceUntilIdle()

        assertFalse(vm.state.value.canSubmitDraft)

        vm.onLotNumber("L-1")
        assertFalse(vm.state.value.canSubmitDraft)

        vm.onExpiryDate("2026-12-31")
        assertFalse(vm.state.value.canSubmitDraft)

        vm.onQuantity("0")
        assertFalse(vm.state.value.canSubmitDraft)

        vm.onQuantity("50")
        assertTrue(vm.state.value.canSubmitDraft)
    }

    @Test
    fun submitAdd_calls_addLot_with_typed_param_and_reloads() = runVmTest { dispatchers ->
        val (vm, repo) = newVm(dispatchers)
        vm.open(drugId = "d1", drugName = "X")
        advanceUntilIdle()
        val initialListCount = repo.listCallCount
        vm.onLotNumber("L-001")
        vm.onExpiryDate("2027-06-30")
        vm.onQuantity("200")
        vm.onCostPrice("12.50")
        vm.onSellPrice("18.00")
        vm.submitAdd()
        advanceUntilIdle()
        val captured = repo.lastAdd
        assertNotNull(captured)
        assertEquals("d1", captured.drugId)
        assertEquals("L-001", captured.lotNumber)
        assertEquals(kotlinx.datetime.LocalDate.parse("2027-06-30"), captured.expiryDate)
        assertEquals(Quantity(200), captured.quantity)
        assertEquals(Money(12.50), captured.costPrice)
        assertEquals(Money(18.00), captured.sellPrice)

        val s = vm.state.value
        assertFalse(s.addFormOpen)
        assertEquals("", s.draft.lotNumber)
        assertEquals("", s.draft.quantity)
        assertFalse(s.saving)

        assertTrue(repo.listCallCount > initialListCount)
        assertTrue(s.lots.any { it.lotNumber == "L-001" })
    }

    @Test
    fun submitAdd_failure_surfaces_error_keeps_draft() = runVmTest { dispatchers ->
        val (vm, _) = newVm(dispatchers, FakeLotsRepository(addThrowsOn = "L-bad"))
        vm.open(drugId = "d1", drugName = "X")
        advanceUntilIdle()
        vm.onLotNumber("L-bad")
        vm.onExpiryDate("2027-01-01")
        vm.onQuantity("10")
        vm.submitAdd()
        advanceUntilIdle()
        val s = vm.state.value
        assertNotNull(s.errorState)
        assertFalse(s.saving)

        assertEquals("L-bad", s.draft.lotNumber)
    }

    @Test
    fun requestDelete_then_confirmDelete_calls_deleteLot_and_reloads() = runVmTest { dispatchers ->
        val (vm, repo) = newVm(
            dispatchers,
            FakeLotsRepository(seed = listOf(lot("a", "d1"), lot("b", "d1"))),
        )
        vm.open(drugId = "d1", drugName = "X")
        advanceUntilIdle()
        val target = vm.state.value.lots.first { it.id == "a" }
        vm.requestDelete(target)
        assertEquals(target, vm.state.value.pendingDelete)
        vm.confirmDelete()
        advanceUntilIdle()
        val captured = repo.lastDelete
        assertNotNull(captured)
        assertEquals("d1", captured.drugId)
        assertEquals("a", captured.lotId)

        val s = vm.state.value
        assertNull(s.pendingDelete)
        assertFalse(s.saving)
        assertTrue(s.lots.none { it.id == "a" })
        assertTrue(s.lots.any { it.id == "b" })
    }

    @Test
    fun cancelDelete_clears_pending_without_calling_repo() = runVmTest { dispatchers ->
        val (vm, repo) = newVm(
            dispatchers,
            FakeLotsRepository(seed = listOf(lot("a", "d1"))),
        )
        vm.open(drugId = "d1", drugName = "X")
        advanceUntilIdle()
        val target = vm.state.value.lots.first()
        vm.requestDelete(target)
        vm.cancelDelete()
        advanceUntilIdle()
        assertNull(vm.state.value.pendingDelete)
        assertNull(repo.lastDelete)
    }
}
