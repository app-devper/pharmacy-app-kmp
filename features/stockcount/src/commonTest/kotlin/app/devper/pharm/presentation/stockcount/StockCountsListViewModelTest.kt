package app.devper.pharm.presentation.stockcount

import app.devper.pharm.domain.model.StockCount
import app.devper.pharm.presentation.stockcount.exception.StockCountUiStateError
import app.devper.pharm.domain.repository.FakeStockCountsRepository
import app.devper.pharm.domain.usecase.inventory.GetStockCountsUseCase
import app.devper.pharm.ui.common.runVmTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertNull

@OptIn(ExperimentalCoroutinesApi::class)
class StockCountsListViewModelTest {

    private fun count(id: String) = StockCount(
        id = id, countNo = "SC-$id", note = "", items = emptyList(), createdAt = kotlinx.datetime.LocalDateTime.parse("2026-06-01T10:00:00"),
    )

    @Test
    fun init_loads_counts() = runVmTest { d ->
        val repo = FakeStockCountsRepository(seed = listOf(count("a"), count("b"), count("c")))
        val vm = StockCountsListViewModel(GetStockCountsUseCase(repo, d))
        advanceUntilIdle()
        assertEquals(3, vm.state.value.counts.size)
        assertFalse(vm.state.value.loading)
        assertNull(vm.state.value.errorState)
    }

    @Test
    fun query_change_updates_state() = runVmTest { d ->
        val vm = StockCountsListViewModel(GetStockCountsUseCase(FakeStockCountsRepository(), d))
        advanceUntilIdle()
        vm.onQueryChange("SC-001")
        assertEquals("SC-001", vm.state.value.query)
    }

    @Test
    fun load_failure_surfaces_typed_error_and_clears_loading() = runVmTest { d ->
        val repo = FakeStockCountsRepository(listThrows = true)
        val vm = StockCountsListViewModel(GetStockCountsUseCase(repo, d))
        advanceUntilIdle()
        assertIs<StockCountUiStateError.LoadRoundsFailed>(vm.state.value.errorState)
        assertFalse(vm.state.value.loading)
    }
}
