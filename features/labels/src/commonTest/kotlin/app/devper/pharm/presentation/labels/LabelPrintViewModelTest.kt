@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)

package app.devper.pharm.presentation.labels

import app.devper.pharm.common.value.Money
import app.devper.pharm.common.value.Quantity

import app.devper.pharm.common.AppDispatchers
import app.devper.pharm.common.error.CommonUiStateError
import app.devper.pharm.common.error.CommonUiStateMessage
import app.devper.pharm.domain.model.Drug
import app.devper.pharm.domain.model.LabelSize
import app.devper.pharm.domain.repository.FakeDrugRepository
import app.devper.pharm.domain.repository.FakeLabelRepository
import app.devper.pharm.domain.usecase.inventory.GetDrugsUseCase
import app.devper.pharm.domain.usecase.inventory.PrintLabelsUseCase
import app.devper.pharm.presentation.labels.exception.LabelPrintUiStateError
import app.devper.pharm.ui.common.runVmTest
import kotlinx.coroutines.test.advanceUntilIdle
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class LabelPrintViewModelTest {

    private fun drug(id: String, name: String = "Drug $id", barcode: String? = "BC$id", price: Double = 5.0): Drug =
        Drug(
            id = id,
            name = name,
            genericName = null,
            type = null,
            strength = null,
            barcode = barcode,
            sellPrice = Money(price),
            costPrice = Money(0.0),
            stock = Quantity(100),
            minStock = Quantity(0),
            unit = "เม็ด",
            regNo = null,
        )

    private fun bundle(
        dispatchers: AppDispatchers,
        drugs: List<Drug> = listOf(drug("d1"), drug("d2"), drug("d3")),
        labelRepo: FakeLabelRepository = FakeLabelRepository(),
    ): Pair<LabelPrintViewModel, FakeLabelRepository> {
        val drugRepo = FakeDrugRepository(seed = drugs)
        val vm = LabelPrintViewModel(
            getDrugs = GetDrugsUseCase(drugRepo, dispatchers),
            printLabels = PrintLabelsUseCase(labelRepo, dispatchers),
        )
        return vm to labelRepo
    }

    @Test
    fun loads_drugs_on_init() = runVmTest { dispatchers ->
        val (vm, _) = bundle(dispatchers)
        advanceUntilIdle()
        val state = vm.state.value
        assertFalse(state.loading)
        assertEquals(3, state.drugs.size)
    }

    @Test
    fun add_drug_creates_line_with_one_copy_using_barcode_and_price() = runVmTest { dispatchers ->
        val (vm, _) = bundle(dispatchers)
        advanceUntilIdle()
        vm.onAddDrug(drug(id = "d1", name = "Paracetamol", barcode = "8851", price = 2.5))
        val state = vm.state.value
        assertEquals(1, state.lines.size)
        val line = state.lines[0]
        assertEquals("d1", line.drugId)
        assertEquals("Paracetamol", line.drugName)
        assertEquals("8851", line.barcode)
        assertEquals(2.5, line.price)
        assertEquals(1, line.copies)
        assertTrue(line.includePrice)
    }

    @Test
    fun add_same_drug_twice_increments_copies_not_lines() = runVmTest { dispatchers ->
        val (vm, _) = bundle(dispatchers)
        advanceUntilIdle()
        val d = drug("d1")
        vm.onAddDrug(d)
        vm.onAddDrug(d)
        vm.onAddDrug(d)
        val state = vm.state.value
        assertEquals(1, state.lines.size)
        assertEquals(3, state.lines[0].copies)
    }

    @Test
    fun add_drug_with_blank_barcode_falls_back_to_drug_id() = runVmTest { dispatchers ->
        val (vm, _) = bundle(dispatchers)
        advanceUntilIdle()
        vm.onAddDrug(drug(id = "no-barcode-here", barcode = ""))
        assertEquals("no-barcode-here", vm.state.value.lines.single().barcode)
    }

    @Test
    fun remove_line_drops_it_and_keeps_rest_in_order() = runVmTest { dispatchers ->
        val (vm, _) = bundle(dispatchers)
        advanceUntilIdle()
        vm.onAddDrug(drug("d1"))
        vm.onAddDrug(drug("d2"))
        vm.onAddDrug(drug("d3"))
        vm.onRemoveLine(1)
        val ids = vm.state.value.lines.map { it.drugId }
        assertEquals(listOf("d1", "d3"), ids)
    }

    @Test
    fun change_copies_clamps_to_zero_and_to_max() = runVmTest { dispatchers ->
        val (vm, _) = bundle(dispatchers)
        advanceUntilIdle()
        vm.onAddDrug(drug("d1"))
        vm.onChangeCopies(0, -5)
        assertEquals(0, vm.state.value.lines[0].copies)
        vm.onChangeCopies(0, 10_000)
        assertEquals(500, vm.state.value.lines[0].copies)
    }

    @Test
    fun change_barcode_persists() = runVmTest { dispatchers ->
        val (vm, _) = bundle(dispatchers)
        advanceUntilIdle()
        vm.onAddDrug(drug("d1"))
        vm.onChangeBarcode(0, "9991234567890")
        assertEquals("9991234567890", vm.state.value.lines[0].barcode)
    }

    @Test
    fun toggle_include_price_persists() = runVmTest { dispatchers ->
        val (vm, _) = bundle(dispatchers)
        advanceUntilIdle()
        vm.onAddDrug(drug("d1"))
        assertTrue(vm.state.value.lines[0].includePrice)
        vm.onToggleIncludePrice(0, false)
        assertFalse(vm.state.value.lines[0].includePrice)
    }

    @Test
    fun query_filters_by_name_or_barcode() = runVmTest { dispatchers ->
        val (vm, _) = bundle(
            dispatchers,
            drugs = listOf(
                drug("d1", name = "Paracetamol", barcode = "8851"),
                drug("d2", name = "Aspirin", barcode = "9001"),
                drug("d3", name = "Ibuprofen", barcode = "8852"),
            ),
        )
        advanceUntilIdle()
        vm.onQueryChange("para")
        assertEquals(listOf("Paracetamol"), vm.state.value.filteredDrugs.map { it.name })
        vm.onQueryChange("885")
        assertEquals(listOf("Paracetamol", "Ibuprofen"), vm.state.value.filteredDrugs.map { it.name })
    }

    @Test
    fun size_change_persists() = runVmTest { dispatchers ->
        val (vm, _) = bundle(dispatchers)
        advanceUntilIdle()
        assertEquals(LabelSize.Small, vm.state.value.size)
        vm.onSizeChange(LabelSize.Medium)
        assertEquals(LabelSize.Medium, vm.state.value.size)
    }

    @Test
    fun clearAll_empties_lines_but_keeps_drugs() = runVmTest { dispatchers ->
        val (vm, _) = bundle(dispatchers)
        advanceUntilIdle()
        vm.onAddDrug(drug("d1"))
        vm.onAddDrug(drug("d2"))
        vm.onClearAll()
        val state = vm.state.value
        assertTrue(state.lines.isEmpty())
        assertEquals(3, state.drugs.size)
    }

    @Test
    fun canPrint_false_when_no_lines() = runVmTest { dispatchers ->
        val (vm, _) = bundle(dispatchers)
        advanceUntilIdle()
        assertFalse(vm.state.value.canPrint)
        assertEquals(0, vm.state.value.totalCopies)
    }

    @Test
    fun canPrint_false_when_all_copies_zero() = runVmTest { dispatchers ->
        val (vm, _) = bundle(dispatchers)
        advanceUntilIdle()
        vm.onAddDrug(drug("d1"))
        vm.onChangeCopies(0, 0)
        assertFalse(vm.state.value.canPrint)
    }

    @Test
    fun print_filters_out_zero_copies_and_surfaces_message_on_success() = runVmTest { dispatchers ->
        val fake = FakeLabelRepository(saveAs = "saved labels.pdf")
        val (vm, _) = bundle(dispatchers, labelRepo = fake)
        advanceUntilIdle()
        vm.onAddDrug(drug("d1"))
        vm.onAddDrug(drug("d2"))
        vm.onChangeCopies(1, 0)
        vm.onPrint()
        advanceUntilIdle()
        assertFalse(vm.state.value.printing)
        val msg = assertIs<CommonUiStateMessage.ExportDone>(vm.state.value.messageState)
        assertEquals("saved labels.pdf", msg.path)
        val param = assertNotNull(fake.lastParam)
        assertEquals(1, param.lines.size)
        assertEquals("d1", param.lines[0].drugId)
    }

    @Test
    fun print_failure_surfaces_error_and_clears_printing() = runVmTest { dispatchers ->
        val fake = FakeLabelRepository(throws = true)
        val (vm, _) = bundle(dispatchers, labelRepo = fake)
        advanceUntilIdle()
        vm.onAddDrug(drug("d1"))
        vm.onPrint()
        advanceUntilIdle()
        val state = vm.state.value
        assertFalse(state.printing)
        assertIs<LabelPrintUiStateError.PrintFailed>(state.errorState)
        assertEquals("printer offline", state.errorState?.cause?.message)
        assertNull(state.messageState)
    }

    @Test
    fun print_no_op_when_canPrint_false() = runVmTest { dispatchers ->
        val fake = FakeLabelRepository()
        val (vm, _) = bundle(dispatchers, labelRepo = fake)
        advanceUntilIdle()
        vm.onPrint()
        advanceUntilIdle()
        assertEquals(0, fake.callCount)
    }

    @Test
    fun load_failure_sets_error_and_clears_loading() = runVmTest { dispatchers ->
        val drugRepo = FakeDrugRepository(listThrows = true)
        val vm = LabelPrintViewModel(
            getDrugs = GetDrugsUseCase(drugRepo, dispatchers),
            printLabels = PrintLabelsUseCase(FakeLabelRepository(), dispatchers),
        )
        advanceUntilIdle()
        assertFalse(vm.state.value.loading)
        assertIs<CommonUiStateError.LoadFailed>(vm.state.value.errorState)
    }

    @Test
    fun reload_triggers_another_drug_list_call() = runVmTest { dispatchers ->
        val drugRepo = FakeDrugRepository(seed = listOf(drug("d1")))
        val vm = LabelPrintViewModel(
            getDrugs = GetDrugsUseCase(drugRepo, dispatchers),
            printLabels = PrintLabelsUseCase(FakeLabelRepository(), dispatchers),
        )
        advanceUntilIdle()
        assertEquals(1, drugRepo.listCallCount)
        vm.reload()
        advanceUntilIdle()
        assertEquals(2, drugRepo.listCallCount)
    }

    @Test
    fun dismiss_message_clears_message_state() = runVmTest { dispatchers ->
        val (vm, _) = bundle(dispatchers, labelRepo = FakeLabelRepository(saveAs = "labels.pdf"))
        advanceUntilIdle()
        vm.onAddDrug(drug("d1"))
        vm.onPrint()
        advanceUntilIdle()
        assertNotNull(vm.state.value.messageState)
        vm.dismissMessage()
        assertNull(vm.state.value.messageState)
    }
}
