package app.devper.pharm.presentation.planning

import app.devper.pharm.domain.event.StockChangeBus
import app.devper.pharm.domain.model.Drug
import app.devper.pharm.domain.repository.FakeDrugRepository
import app.devper.pharm.domain.usecase.GetLowStockDrugsUseCase
import app.devper.pharm.ui.common.runVmTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

@OptIn(ExperimentalCoroutinesApi::class)
class LowStockViewModelTest {

    private fun drug(id: String) = Drug(
        id = id, name = "Drug $id", genericName = null, type = null, strength = null,
        barcode = null, sellPrice = 2.0, costPrice = 1.0, stock = 1, minStock = 5,
        unit = "เม็ด", regNo = null,
    )

    @Test
    fun init_loads_low_stock_drugs() = runVmTest { d ->
        val repo = FakeDrugRepository(lowStockSeed = listOf(drug("a"), drug("b"), drug("c")))
        val vm = LowStockViewModel(GetLowStockDrugsUseCase(repo, d), StockChangeBus())
        advanceUntilIdle()
        assertEquals(3, vm.state.value.drugs.size)
        assertFalse(vm.state.value.loading)
    }

    @Test
    fun stock_change_event_triggers_reload() = runVmTest { d ->
        val repo = FakeDrugRepository(lowStockSeed = listOf(drug("a")))
        val bus = StockChangeBus()
        val vm = LowStockViewModel(GetLowStockDrugsUseCase(repo, d), bus)
        advanceUntilIdle()
        bus.emit()
        advanceUntilIdle()
        assertEquals(1, vm.state.value.drugs.size)
        assertFalse(vm.state.value.loading)
    }
}
