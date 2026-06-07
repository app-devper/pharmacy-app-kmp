package app.devper.pharm.presentation.stock

import app.devper.pharm.common.value.Money
import app.devper.pharm.common.value.Quantity

import app.devper.pharm.domain.model.Drug
import app.devper.pharm.domain.repository.FakeDrugRepository
import app.devper.pharm.domain.usecase.GetDrugsUseCase
import app.devper.pharm.ui.common.runVmTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull

@OptIn(ExperimentalCoroutinesApi::class)
class StockViewModelTest {

    private fun drug(id: String) = Drug(
        id = id, name = "Drug $id", genericName = null, type = null, strength = null,
        barcode = null, sellPrice = Money(2.0), costPrice = Money(1.0), stock = Quantity(10), minStock = Quantity(5),
        unit = "เม็ด", regNo = null,
    )

    @Test
    fun init_loads_drugs() = runVmTest { d ->
        val repo = FakeDrugRepository(seed = listOf(drug("a"), drug("b")))
        val vm = StockViewModel(GetDrugsUseCase(repo, d))
        advanceUntilIdle()
        assertEquals(2, vm.state.value.drugs.size)
        assertFalse(vm.state.value.loading)
        assertNull(vm.state.value.error)
    }

    @Test
    fun query_and_type_filter_update_state() = runVmTest { d ->
        val vm = StockViewModel(GetDrugsUseCase(FakeDrugRepository(), d))
        advanceUntilIdle()
        vm.onQueryChange("para")
        vm.onTypeFilterChange(StockTypeFilter.Herb)
        assertEquals("para", vm.state.value.query)
        assertEquals(StockTypeFilter.Herb, vm.state.value.typeFilter)
    }
}
