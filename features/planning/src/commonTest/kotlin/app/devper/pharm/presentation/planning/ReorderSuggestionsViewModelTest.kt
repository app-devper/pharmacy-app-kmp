package app.devper.pharm.presentation.planning

import app.devper.pharm.common.value.Money
import app.devper.pharm.common.value.Quantity

import app.devper.pharm.domain.model.ReorderSuggestion
import app.devper.pharm.domain.observer.PurchaseDraftProvider
import app.devper.pharm.domain.repository.FakeDrugRepository
import app.devper.pharm.domain.usecase.inventory.GetReorderSuggestionsUseCase
import app.devper.pharm.ui.common.runVmTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull

@OptIn(ExperimentalCoroutinesApi::class)
class ReorderSuggestionsViewModelTest {

    private fun suggestion(id: String) = ReorderSuggestion(
        drugId = id, drugName = "Drug $id", unit = "เม็ด",
        currentStock = Quantity(0), minStock = Quantity(50), qtySold = Quantity(100), avgDailySale = 3.3,
        daysLeft = 0.0, suggestedQty = Quantity(100), costPrice = Money(1.0), sellPrice = Money(2.0),
    )

    @Test
    fun init_loads_suggestions() = runVmTest { d ->
        val repo = FakeDrugRepository(reorderSeed = listOf(suggestion("a"), suggestion("b")))
        val vm = ReorderSuggestionsViewModel(GetReorderSuggestionsUseCase(repo, d), PurchaseDraftProvider())
        advanceUntilIdle()
        assertEquals(2, vm.state.value.suggestions.size)
        assertFalse(vm.state.value.loading)
    }

    @Test
    fun empty_seed_yields_empty_list() = runVmTest { d ->
        val vm = ReorderSuggestionsViewModel(GetReorderSuggestionsUseCase(FakeDrugRepository(), d), PurchaseDraftProvider())
        advanceUntilIdle()
        assertEquals(0, vm.state.value.suggestions.size)
        assertFalse(vm.state.value.loading)
    }

    @Test
    fun load_failure_sets_error_state_and_clears_loading() = runVmTest { d ->
        val repo = FakeDrugRepository(reorderThrows = true)
        val vm = ReorderSuggestionsViewModel(GetReorderSuggestionsUseCase(repo, d), PurchaseDraftProvider())
        advanceUntilIdle()
        assertNotNull(vm.state.value.errorState)
        assertFalse(vm.state.value.loading)
    }

    @Test
    fun reload_refreshes_list_from_repo() = runVmTest { d ->
        val repo = FakeDrugRepository(reorderSeed = listOf(suggestion("a")))
        val vm = ReorderSuggestionsViewModel(GetReorderSuggestionsUseCase(repo, d), PurchaseDraftProvider())
        advanceUntilIdle()
        assertEquals(1, vm.state.value.suggestions.size)
        vm.reload()
        advanceUntilIdle()
        assertFalse(vm.state.value.loading)
        assertNotNull(repo.lastReorderParam)
    }

    @Test
    fun add_to_purchase_order_seeds_draft_and_updates_count() = runVmTest { d ->
        val repo = FakeDrugRepository(reorderSeed = listOf(suggestion("a"), suggestion("b")))
        val draft = PurchaseDraftProvider()
        val vm = ReorderSuggestionsViewModel(GetReorderSuggestionsUseCase(repo, d), draft)
        advanceUntilIdle()
        vm.addToPurchaseOrder(vm.state.value.suggestions.first())
        advanceUntilIdle()
        assertEquals(1, draft.state.value.size)
        assertEquals(1, vm.state.value.draftCount)
    }

    @Test
    fun add_all_seeds_every_suggestion_deduplicated() = runVmTest { d ->
        val repo = FakeDrugRepository(reorderSeed = listOf(suggestion("a"), suggestion("b")))
        val draft = PurchaseDraftProvider()
        val vm = ReorderSuggestionsViewModel(GetReorderSuggestionsUseCase(repo, d), draft)
        advanceUntilIdle()
        vm.addAllToPurchaseOrder()
        vm.addAllToPurchaseOrder()
        advanceUntilIdle()
        assertEquals(2, draft.state.value.size)
    }
}
