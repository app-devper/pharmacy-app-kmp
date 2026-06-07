package app.devper.pharm.presentation.sell.flow

import app.devper.pharm.common.value.Money
import app.devper.pharm.common.value.Quantity

import app.devper.pharm.common.AppDispatchers
import app.devper.pharm.domain.event.StockChangeBus
import app.devper.pharm.domain.model.AltUnit
import app.devper.pharm.domain.model.Drug
import app.devper.pharm.domain.repository.FakeCartRepository
import app.devper.pharm.domain.repository.FakeDrugRepository
import app.devper.pharm.domain.usecase.AddToCartUseCase
import app.devper.pharm.domain.usecase.GetDrugsUseCase
import app.devper.pharm.ui.common.runVmTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.advanceUntilIdle
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class DrugPickerViewModelTest {

    private fun drug(
        id: String = "d1",
        name: String = "Paracetamol",
        barcode: String? = "BARCODE-001",
        regNo: String? = "REG-001",
        altUnits: List<AltUnit> = emptyList(),
        stock: Int = 100,
    ) = Drug(
        id = id, name = name, genericName = null, type = null, strength = null,
        barcode = barcode, sellPrice = Money(5.0), costPrice = Money(0.0), stock = Quantity(stock),
        minStock = Quantity(0), unit = "เม็ด", regNo = regNo, altUnits = altUnits,
    )

    private data class Bundle(
        val vm: DrugPickerViewModel,
        val cart: FakeCartRepository,
        val repo: FakeDrugRepository,
        val bus: StockChangeBus,
    )

    private fun newVm(
        dispatchers: AppDispatchers,
        repo: FakeDrugRepository = FakeDrugRepository(),
        cart: FakeCartRepository = FakeCartRepository(),
        bus: StockChangeBus = StockChangeBus(),
    ): Bundle {
        val vm = DrugPickerViewModel(
            getDrugs = GetDrugsUseCase(repo, dispatchers),
            addToCart = AddToCartUseCase(cart),
            stockChangeBus = bus,
        )
        return Bundle(vm, cart, repo, bus)
    }

    @Test
    fun init_loads_drug_list() = runVmTest { dispatchers ->
        val seed = listOf(drug(id = "a", name = "Aspirin"), drug(id = "b", name = "Bactrim"))
        val (vm, _, repo) = newVm(dispatchers, repo = FakeDrugRepository(seed = seed))
        advanceUntilIdle()
        assertEquals(2, vm.state.value.drugs.size)
        assertFalse(vm.state.value.drugsLoading)
        assertEquals(1, repo.listCallCount)
    }

    @Test
    fun load_failure_routes_to_error_clears_spinner() = runVmTest { dispatchers ->
        val (vm) = newVm(dispatchers, repo = FakeDrugRepository(listThrows = true))
        advanceUntilIdle()
        assertNotNull(vm.state.value.error)
        assertEquals("list failed", vm.state.value.error)
        assertFalse(vm.state.value.drugsLoading)
    }

    @Test
    fun onQueryChange_drives_filteredDrugs_via_uistate() = runVmTest { dispatchers ->
        val seed = listOf(drug(id = "a", name = "Aspirin"), drug(id = "p", name = "Paracetamol"))
        val (vm) = newVm(dispatchers, repo = FakeDrugRepository(seed = seed))
        advanceUntilIdle()
        assertEquals(2, vm.state.value.filteredDrugs.size)
        vm.onQueryChange("para")
        assertEquals("para", vm.state.value.query)
        assertEquals(1, vm.state.value.filteredDrugs.size)
        assertEquals("Paracetamol", vm.state.value.filteredDrugs[0].name)
    }

    @Test
    fun onTapDrug_with_no_altUnits_adds_base_unit_straight_to_cart() = runVmTest { dispatchers ->
        val d = drug()
        val (vm, cart) = newVm(dispatchers, repo = FakeDrugRepository(seed = listOf(d)))
        advanceUntilIdle()
        vm.onTapDrug(d)
        advanceUntilIdle()
        assertEquals(d, cart.lastAdd?.drug)
        assertNull(cart.lastAdd?.altUnit)

        assertNull(vm.state.value.altUnitPickerFor)
    }

    @Test
    fun onTapDrug_with_visible_altUnits_opens_picker_sheet_without_adding() = runVmTest { dispatchers ->
        val alt = AltUnit(name = "แผง", factor = 10, sellPrice = Money(45.0))
        val d = drug(altUnits = listOf(alt))
        val (vm, cart) = newVm(dispatchers, repo = FakeDrugRepository(seed = listOf(d)))
        advanceUntilIdle()
        vm.onTapDrug(d)
        advanceUntilIdle()

        assertEquals(d, vm.state.value.altUnitPickerFor)
        assertNull(cart.lastAdd)
    }

    @Test
    fun onTapDrug_with_all_hidden_altUnits_falls_through_to_base_unit_add() = runVmTest { dispatchers ->
        val alt = AltUnit(name = "ซ่อน", factor = 5, sellPrice = Money(0.0), hidden = true)
        val d = drug(altUnits = listOf(alt))
        val (vm, cart) = newVm(dispatchers, repo = FakeDrugRepository(seed = listOf(d)))
        advanceUntilIdle()
        vm.onTapDrug(d)
        advanceUntilIdle()

        assertNull(vm.state.value.altUnitPickerFor)
        assertEquals(d, cart.lastAdd?.drug)
        assertNull(cart.lastAdd?.altUnit)
    }

    @Test
    fun onPickAltUnit_adds_that_unit_and_closes_sheet() = runVmTest { dispatchers ->
        val alt = AltUnit(name = "แผง", factor = 10, sellPrice = Money(45.0))
        val d = drug(altUnits = listOf(alt))
        val (vm, cart) = newVm(dispatchers, repo = FakeDrugRepository(seed = listOf(d)))
        advanceUntilIdle()
        vm.onTapDrug(d)
        vm.onPickAltUnit(alt)
        advanceUntilIdle()
        assertEquals(d, cart.lastAdd?.drug)
        assertEquals(alt, cart.lastAdd?.altUnit)
        assertNull(vm.state.value.altUnitPickerFor)
    }

    @Test
    fun onPickAltUnit_null_adds_base_unit() = runVmTest { dispatchers ->
        val alt = AltUnit(name = "แผง", factor = 10, sellPrice = Money(45.0))
        val d = drug(altUnits = listOf(alt))
        val (vm, cart) = newVm(dispatchers, repo = FakeDrugRepository(seed = listOf(d)))
        advanceUntilIdle()
        vm.onTapDrug(d)
        vm.onPickAltUnit(null)
        advanceUntilIdle()
        assertEquals(d, cart.lastAdd?.drug)
        assertNull(cart.lastAdd?.altUnit)
        assertNull(vm.state.value.altUnitPickerFor)
    }

    @Test
    fun onScanBarcode_match_adds_drug() = runVmTest { dispatchers ->
        val d = drug(barcode = "8851234567890")
        val (vm, cart) = newVm(dispatchers, repo = FakeDrugRepository(seed = listOf(d)))
        advanceUntilIdle()
        vm.onScanBarcode("8851234567890")
        advanceUntilIdle()
        assertEquals(d, cart.lastAdd?.drug)
    }

    @Test
    fun successful_add_emits_added_event_with_drug_name() = runVmTest { dispatchers ->
        val d = drug(name = "Amoxicillin")
        val (vm, _) = newVm(dispatchers, repo = FakeDrugRepository(seed = listOf(d)))
        advanceUntilIdle()
        vm.onTapDrug(d)
        assertEquals("Amoxicillin", vm.added.first())
    }

    @Test
    fun onScanBarcode_no_match_surfaces_error() = runVmTest { dispatchers ->
        val (vm, cart) = newVm(dispatchers, repo = FakeDrugRepository(seed = listOf(drug())))
        advanceUntilIdle()
        vm.onScanBarcode("UNKNOWN-9999")
        advanceUntilIdle()
        assertNull(cart.lastAdd)
        assertTrue(vm.state.value.error?.contains("UNKNOWN-9999") == true)
    }

    @Test
    fun onScanBarcode_alt_unit_match_adds_that_unit() = runVmTest { dispatchers ->
        val alt = AltUnit(name = "แผง", factor = 10, sellPrice = Money(45.0), barcode = "ALT-001")
        val d = drug(barcode = "DRUG-001", altUnits = listOf(alt))
        val (vm, cart) = newVm(dispatchers, repo = FakeDrugRepository(seed = listOf(d)))
        advanceUntilIdle()
        vm.onScanBarcode("ALT-001")
        advanceUntilIdle()

        assertEquals(d, cart.lastAdd?.drug)
        assertEquals(alt, cart.lastAdd?.altUnit)
    }

    @Test
    fun stockChangeBus_emit_triggers_reload() = runVmTest { dispatchers ->
        val bus = StockChangeBus()
        val (vm, _, repo) = newVm(
            dispatchers,
            repo = FakeDrugRepository(seed = listOf(drug())),
            bus = bus,
        )
        advanceUntilIdle()
        val initialCalls = repo.listCallCount
        bus.emit()
        advanceUntilIdle()
        assertTrue(
            repo.listCallCount > initialCalls,
            "expected list() to be re-invoked on bus emit; was=$initialCalls now=${repo.listCallCount}",
        )

        assertNotNull(vm.state.value)
    }

    @Test
    fun dismissError_clears_error() = runVmTest { dispatchers ->
        val (vm) = newVm(dispatchers, repo = FakeDrugRepository(listThrows = true))
        advanceUntilIdle()
        assertNotNull(vm.state.value.error)
        vm.dismissError()
        assertNull(vm.state.value.error)
    }
}
