package app.devper.pharm.presentation.stock

import app.devper.pharm.common.value.Money
import app.devper.pharm.common.value.Quantity

import app.devper.pharm.domain.model.Drug
import app.devper.pharm.domain.model.ExpiringLot
import app.devper.pharm.domain.repository.FakeDrugRepository
import app.devper.pharm.domain.repository.FakeExpiringLotsRepository
import app.devper.pharm.domain.usecase.inventory.GetDrugsUseCase
import app.devper.pharm.domain.usecase.inventory.GetExpiringLotsUseCase
import app.devper.pharm.domain.usecase.reports.ExportDrugsCsvUseCase
import app.devper.pharm.domain.repository.FakeExportRepository
import app.devper.pharm.ui.common.runVmTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertFalse
import kotlin.test.assertNull
import app.devper.pharm.common.AppDispatchers
import app.devper.pharm.presentation.stock.exception.StockUiStateError

@OptIn(ExperimentalCoroutinesApi::class)
class StockViewModelTest {

    private fun drug(id: String) = Drug(
        id = id, name = "Drug $id", genericName = null, type = null, strength = null,
        barcode = null, sellPrice = Money(2.0), costPrice = Money(1.0), stock = Quantity(10), minStock = Quantity(5),
        unit = "เม็ด", regNo = null,
    )

    private fun expiringLot(id: String) = ExpiringLot(
        id = id, drugId = "d$id", drugName = "Drug $id", lotNumber = "L$id",
        expiryDate = null, remaining = 5, daysLeft = 30,
    )

    private fun vm(
        d: AppDispatchers,
        drugRepo: FakeDrugRepository = FakeDrugRepository(),
        expiringRepo: FakeExpiringLotsRepository = FakeExpiringLotsRepository(),
        exportRepo: FakeExportRepository = FakeExportRepository(),
    ) = StockViewModel(
        GetDrugsUseCase(drugRepo, d),
        GetExpiringLotsUseCase(expiringRepo, d),
        ExportDrugsCsvUseCase(exportRepo, d),
    )

    @Test
    fun init_loads_drugs() = runVmTest { d ->
        val vm = vm(d, drugRepo = FakeDrugRepository(seed = listOf(drug("a"), drug("b"))))
        advanceUntilIdle()
        assertEquals(2, vm.state.value.drugs.size)
        assertFalse(vm.state.value.loading)
        assertNull(vm.state.value.errorState)
    }

    @Test
    fun query_and_type_filter_update_state() = runVmTest { d ->
        val vm = vm(d)
        advanceUntilIdle()
        vm.onQueryChange("para")
        vm.onTypeFilterChange(StockTypeFilter.Herb)
        assertEquals("para", vm.state.value.query)
        assertEquals(StockTypeFilter.Herb, vm.state.value.typeFilter)
    }

    @Test
    fun init_counts_expiring_lots_within_window() = runVmTest { d ->
        val expiring = FakeExpiringLotsRepository(seed = listOf(expiringLot("1"), expiringLot("2")))
        val vm = vm(d, expiringRepo = expiring)
        advanceUntilIdle()
        assertEquals(2, vm.state.value.expiringSoonCount)
        assertEquals(90, expiring.lastFilter?.daysAhead)
    }

    @Test
    fun expiring_load_failure_hides_metric_without_page_error() = runVmTest { d ->
        val vm = vm(d, expiringRepo = FakeExpiringLotsRepository(listThrows = true))
        advanceUntilIdle()
        assertNull(vm.state.value.expiringSoonCount)
        assertNull(vm.state.value.errorState)
    }

    @Test
    fun drug_load_failure_uses_stock_specific_error() = runVmTest { d ->
        val vm = vm(d, drugRepo = FakeDrugRepository(listThrows = true))
        advanceUntilIdle()
        assertIs<StockUiStateError.LoadStockFailed>(vm.state.value.errorState)
        assertFalse(vm.state.value.loading)
    }

    @Test
    fun export_with_no_visible_drugs_emits_empty_message() = runVmTest { d ->
        val vm = vm(d)
        advanceUntilIdle()
        vm.onExportExcel(emptyList())
        advanceUntilIdle()
        assertEquals(app.devper.pharm.common.error.CommonUiStateMessage.ExportEmpty, vm.state.value.messageState)
    }

    @Test
    fun export_with_drugs_produces_done_message() = runVmTest { d ->
        val vm = vm(d, drugRepo = FakeDrugRepository(seed = listOf(drug("a"), drug("b"))))
        advanceUntilIdle()
        vm.onExportExcel(listOf("name"))
        advanceUntilIdle()
        assertIs<app.devper.pharm.common.error.CommonUiStateMessage.ExportDone>(vm.state.value.messageState)
        assertFalse(vm.state.value.exporting)
    }
}
