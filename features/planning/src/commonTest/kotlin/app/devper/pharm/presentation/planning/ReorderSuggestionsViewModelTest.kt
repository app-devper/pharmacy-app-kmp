package app.devper.pharm.presentation.planning

import app.devper.pharm.common.value.Money
import app.devper.pharm.common.value.Quantity

import app.devper.pharm.domain.model.ReorderSuggestion
import app.devper.pharm.domain.repository.FakeDrugRepository
import app.devper.pharm.domain.usecase.inventory.GetReorderSuggestionsUseCase
import app.devper.pharm.ui.common.runVmTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

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
        val vm = ReorderSuggestionsViewModel(GetReorderSuggestionsUseCase(repo, d))
        advanceUntilIdle()
        assertEquals(2, vm.state.value.suggestions.size)
        assertFalse(vm.state.value.loading)
    }

    @Test
    fun empty_seed_yields_empty_list() = runVmTest { d ->
        val vm = ReorderSuggestionsViewModel(GetReorderSuggestionsUseCase(FakeDrugRepository(), d))
        advanceUntilIdle()
        assertEquals(0, vm.state.value.suggestions.size)
        assertFalse(vm.state.value.loading)
    }
}
