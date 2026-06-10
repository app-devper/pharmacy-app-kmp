package app.devper.pharm.presentation.stock

import app.devper.pharm.presentation.stock.exception.DrugFormUiStateError

import app.devper.pharm.common.value.Money
import app.devper.pharm.common.value.Quantity

import app.devper.pharm.common.AppDispatchers
import app.devper.pharm.domain.model.AltUnit
import app.devper.pharm.domain.model.Drug
import app.devper.pharm.domain.repository.FakeDrugRepository
import app.devper.pharm.domain.usecase.inventory.AddDrugUseCase
import app.devper.pharm.domain.usecase.inventory.GetDrugsUseCase
import app.devper.pharm.domain.usecase.inventory.UpdateDrugUseCase
import app.devper.pharm.ui.common.runVmTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class DrugFormViewModelTest {

    private fun drug(
        id: String = "d1",
        name: String = "Paracetamol",
        sellPrice: Money = Money(5.0),
        prices: Map<String, Money> = emptyMap(),
        altUnits: List<AltUnit> = emptyList(),
        reportTypes: List<String> = emptyList(),
    ) = Drug(
        id = id,
        name = name,
        genericName = "Paracetamol BP",
        type = "cur",
        strength = "500mg",
        barcode = "8851234567001",
        sellPrice = sellPrice,
        costPrice = Money(2.0),
        stock = Quantity(100),
        minStock = Quantity(20),
        unit = "เม็ด",
        regNo = "1A 123/45",
        prices = prices,
        altUnits = altUnits,
        reportTypes = reportTypes,
    )

    private data class Bundle(
        val vm: DrugFormViewModel,
        val repo: FakeDrugRepository,
    )

    private fun newVm(
        dispatchers: AppDispatchers,
        repo: FakeDrugRepository = FakeDrugRepository(),
    ): Bundle {
        val vm = DrugFormViewModel(
            getDrugs = GetDrugsUseCase(repo, dispatchers),
            addDrug = AddDrugUseCase(repo, dispatchers),
            updateDrug = UpdateDrugUseCase(repo, dispatchers),
        )
        return Bundle(vm, repo)
    }

    @Test
    fun add_mode_starts_with_empty_form() = runVmTest { dispatchers ->
        val (vm, _) = newVm(dispatchers)
        vm.init(DrugFormMode.Add)
        advanceUntilIdle()
        val s = vm.state.value
        assertTrue(s.mode is DrugFormMode.Add)
        assertEquals("", s.form.name)
        assertFalse(s.canSubmit)
    }

    @Test
    fun edit_mode_hydrates_form_from_drug() = runVmTest { dispatchers ->
        val seeded = drug(prices = mapOf("retail" to Money(5.0), "wholesale" to Money(4.0)))
        val (vm, _) = newVm(dispatchers, FakeDrugRepository(seed = listOf(seeded)))
        vm.init(DrugFormMode.Edit("d1"))
        advanceUntilIdle()
        val f = vm.state.value.form
        assertEquals("Paracetamol", f.name)
        assertEquals("Paracetamol BP", f.genericName)
        assertEquals("500mg", f.strength)
        assertEquals("8851234567001", f.barcode)
        assertEquals("เม็ด", f.unit)
        assertEquals("5", f.sellPrice)
        assertEquals("5", f.tierRetail)
        assertEquals("4", f.tierWholesale)
        assertIs<DrugFormMode.Edit>(vm.state.value.mode)
    }

    @Test
    fun edit_mode_drug_not_found_surfaces_error() = runVmTest { dispatchers ->
        val (vm, _) = newVm(dispatchers, FakeDrugRepository(seed = emptyList()))
        vm.init(DrugFormMode.Edit("does-not-exist"))
        advanceUntilIdle()
        assertIs<DrugFormUiStateError.NotFound>(vm.state.value.errorState)
        assertFalse(vm.state.value.loading)
    }

    @Test
    fun onSellPrice_strips_non_numeric_chars_and_caps_one_dot() = runVmTest { dispatchers ->
        val (vm, _) = newVm(dispatchers)
        vm.init(DrugFormMode.Add)
        advanceUntilIdle()
        vm.onSellPrice("12.5.7abc")
        assertEquals("12.57", vm.state.value.form.sellPrice)
    }

    @Test
    fun onMinStock_strips_to_int_only() = runVmTest { dispatchers ->
        val (vm, _) = newVm(dispatchers)
        vm.init(DrugFormMode.Add)
        advanceUntilIdle()
        vm.onMinStock("10.5abc")
        assertEquals("105", vm.state.value.form.minStock)
    }

    @Test
    fun canSubmit_requires_name_and_sellPrice() = runVmTest { dispatchers ->
        val (vm, _) = newVm(dispatchers)
        vm.init(DrugFormMode.Add)
        advanceUntilIdle()
        assertFalse(vm.state.value.canSubmit)
        vm.onName("Paracetamol")
        assertFalse(vm.state.value.canSubmit)
        vm.onSellPrice("5")
        assertTrue(vm.state.value.canSubmit)
    }

    @Test
    fun canSubmit_false_when_alt_unit_factor_lt_2() = runVmTest { dispatchers ->
        val (vm, _) = newVm(dispatchers)
        vm.init(DrugFormMode.Add)
        advanceUntilIdle()
        vm.onName("Drug")
        vm.onSellPrice("5")
        vm.onAddAltUnit()
        vm.onAltUnitName(0, "แผง")
        vm.onAltUnitFactor(0, "1")
        assertFalse(vm.state.value.canSubmit)
        vm.onAltUnitFactor(0, "10")
        assertTrue(vm.state.value.canSubmit)
    }

    @Test
    fun canSubmit_false_when_alt_unit_name_duplicates_base_unit() = runVmTest { dispatchers ->
        val (vm, _) = newVm(dispatchers)
        vm.init(DrugFormMode.Add)
        advanceUntilIdle()
        vm.onName("Drug")
        vm.onSellPrice("5")
        vm.onUnit("เม็ด")
        vm.onAddAltUnit()
        vm.onAltUnitName(0, "เม็ด")
        vm.onAltUnitFactor(0, "10")
        assertFalse(vm.state.value.canSubmit)
    }

    @Test
    fun toggleReportType_adds_then_removes() = runVmTest { dispatchers ->
        val (vm, _) = newVm(dispatchers)
        vm.init(DrugFormMode.Add)
        advanceUntilIdle()
        vm.onToggleReportType("ky10")
        assertTrue("ky10" in vm.state.value.form.reportTypes)
        vm.onToggleReportType("ky10")
        assertFalse("ky10" in vm.state.value.form.reportTypes)
    }

    @Test
    fun add_alt_unit_then_remove_by_index() = runVmTest { dispatchers ->
        val (vm, _) = newVm(dispatchers)
        vm.init(DrugFormMode.Add)
        advanceUntilIdle()
        vm.onAddAltUnit()
        vm.onAddAltUnit()
        vm.onAltUnitName(0, "กล่อง")
        vm.onAltUnitName(1, "แผง")
        vm.onRemoveAltUnit(0)
        val list = vm.state.value.form.altUnits
        assertEquals(1, list.size)
        assertEquals("แผง", list[0].name)
    }

    @Test
    fun submit_add_happy_path_sends_AddDrugParam_and_marks_saved() = runVmTest { dispatchers ->
        val (vm, repo) = newVm(dispatchers)
        vm.init(DrugFormMode.Add)
        advanceUntilIdle()
        vm.onName("New Drug")
        vm.onGenericName("Generic")
        vm.onSellPrice("12.50")
        vm.onCostPrice("8")
        vm.onMinStock("5")
        vm.onBarcode("885000")
        vm.onUnit("เม็ด")
        vm.onTierRetail("12.50")
        vm.onTierWholesale("10")
        vm.submit()
        advanceUntilIdle()
        val p = repo.lastAdd
        assertNotNull(p)
        assertEquals("New Drug", p.name)
        assertEquals(Money(12.50), p.sellPrice)
        assertEquals(Money(8.0), p.costPrice)
        assertEquals(Quantity(5), p.minStock)
        assertEquals("เม็ด", p.unit)
        assertEquals(Money(12.50), p.prices["retail"])
        assertEquals(Money(10.0), p.prices["wholesale"])
        assertNull(p.createLot)
        assertTrue(vm.state.value.saved)
        assertFalse(vm.state.value.saving)
    }

    @Test
    fun submit_add_with_initial_stock_attaches_createLot_payload() = runVmTest { dispatchers ->
        val (vm, repo) = newVm(dispatchers)
        vm.init(DrugFormMode.Add)
        advanceUntilIdle()
        vm.onName("Stocked")
        vm.onSellPrice("5")
        vm.onInitialStock("50")
        vm.onLotNumber("L-1")
        vm.onLotExpiry("2027-12-31")
        vm.onLotQty("50")
        vm.onLotCostPrice("3")
        vm.submit()
        advanceUntilIdle()
        val lot = repo.lastAdd?.createLot
        assertNotNull(lot)
        assertEquals("L-1", lot.lotNumber)
        assertEquals(kotlinx.datetime.LocalDate.parse("2027-12-31"), lot.expiryDate)
        assertEquals(Quantity(50), lot.quantity)
        assertEquals(Money(3.0), lot.costPrice)
    }

    @Test
    fun submit_edit_happy_path_calls_updateDrug_with_correct_id() = runVmTest { dispatchers ->
        val seeded = drug(id = "d99")
        val (vm, repo) = newVm(dispatchers, FakeDrugRepository(seed = listOf(seeded)))
        vm.init(DrugFormMode.Edit("d99"))
        advanceUntilIdle()
        vm.onName("Renamed")
        vm.submit()
        advanceUntilIdle()
        val p = repo.lastUpdate
        assertNotNull(p)
        assertEquals("d99", p.id)
        assertEquals("Renamed", p.name)
        assertTrue(vm.state.value.saved)
    }

    @Test
    fun submit_failure_surfaces_error_does_not_mark_saved() = runVmTest { dispatchers ->
        val (vm, _) = newVm(
            dispatchers,
            FakeDrugRepository(addThrowsOn = "BAD"),
        )
        vm.init(DrugFormMode.Add)
        advanceUntilIdle()
        vm.onName("Test")
        vm.onSellPrice("5")
        vm.onBarcode("BAD")
        vm.submit()
        advanceUntilIdle()
        assertFalse(vm.state.value.saved)
        assertNotNull(vm.state.value.errorState)
        assertFalse(vm.state.value.saving)
    }

    @Test
    fun dismissError_clears_error() = runVmTest { dispatchers ->
        val (vm, _) = newVm(dispatchers)
        vm.init(DrugFormMode.Edit("missing"))
        advanceUntilIdle()
        assertNotNull(vm.state.value.errorState)
        vm.dismissError()
        assertNull(vm.state.value.errorState)
    }
}
