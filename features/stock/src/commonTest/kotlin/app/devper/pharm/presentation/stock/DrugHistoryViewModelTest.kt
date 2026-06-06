package app.devper.pharm.presentation.stock

import app.devper.pharm.common.AppDispatchers
import app.devper.pharm.domain.model.MovementType
import app.devper.pharm.domain.model.StockMovement
import app.devper.pharm.domain.model.StockMovementsPage
import app.devper.pharm.domain.param.MovementsFilterParam
import app.devper.pharm.domain.repository.MovementsRepository
import app.devper.pharm.domain.usecase.GetMovementsUseCase
import app.devper.pharm.ui.common.runVmTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull

@OptIn(ExperimentalCoroutinesApi::class)
class DrugHistoryViewModelTest {

    private fun newVm(
        dispatchers: AppDispatchers,
        repo: FakeMovementsRepository = FakeMovementsRepository(),
    ): Pair<DrugHistoryViewModel, FakeMovementsRepository> {
        val vm = DrugHistoryViewModel(getMovements = GetMovementsUseCase(repo, dispatchers))
        return vm to repo
    }

    private fun movement(id: String) = StockMovement(
        id = id, type = MovementType.Sale, drugId = "d1", drugName = "Paracetamol",
        delta = -5, reference = "SC-$id", note = "by: ภ.", at = "2026-05-17T14:00:00",
    )

    @Test
    fun load_populates_items_and_filters_by_drug_name() = runVmTest { dispatchers ->
        val repo = FakeMovementsRepository(
            page = StockMovementsPage(listOf(movement("a"), movement("b")), total = 2),
        )
        val (vm, _) = newVm(dispatchers, repo)
        vm.load("Paracetamol")
        advanceUntilIdle()
        val s = vm.state.value
        assertFalse(s.loading)
        assertNull(s.error)
        assertEquals("Paracetamol", s.drugName)
        assertEquals(2, s.items.size)
        assertEquals("Paracetamol", repo.lastFilter?.drugName)
    }

    @Test
    fun load_failure_surfaces_error_and_clears_spinner() = runVmTest { dispatchers ->
        val (vm, _) = newVm(dispatchers, FakeMovementsRepository(failWith = RuntimeException("boom")))
        vm.load("Paracetamol")
        advanceUntilIdle()
        val s = vm.state.value
        assertFalse(s.loading)
        assertNotNull(s.error)
    }
}

private class FakeMovementsRepository(
    private val page: StockMovementsPage = StockMovementsPage(emptyList(), 0),
    private val failWith: Throwable? = null,
) : MovementsRepository {
    var lastFilter: MovementsFilterParam? = null
        private set

    override suspend fun list(filter: MovementsFilterParam): StockMovementsPage {
        lastFilter = filter
        failWith?.let { throw it }
        return page
    }
}
