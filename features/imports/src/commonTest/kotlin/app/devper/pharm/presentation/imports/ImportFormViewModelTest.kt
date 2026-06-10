package app.devper.pharm.presentation.imports

import app.devper.pharm.common.value.Money
import app.devper.pharm.common.value.Quantity

import app.devper.pharm.common.AppDispatchers
import app.devper.pharm.domain.model.Drug
import app.devper.pharm.domain.model.PurchaseOrder
import app.devper.pharm.domain.model.PurchaseOrderItem
import app.devper.pharm.domain.model.PurchaseOrderStatus
import app.devper.pharm.domain.repository.FakeDrugRepository
import app.devper.pharm.domain.repository.FakePurchaseOrderRepository
import app.devper.pharm.domain.repository.FakeSupplierRepository
import app.devper.pharm.domain.usecase.purchasing.AddPurchaseOrderUseCase
import app.devper.pharm.domain.usecase.inventory.GetDrugsUseCase
import app.devper.pharm.domain.usecase.purchasing.GetPurchaseOrderUseCase
import app.devper.pharm.domain.usecase.suppliers.GetSuppliersUseCase
import app.devper.pharm.domain.usecase.purchasing.UpdatePurchaseOrderUseCase
import app.devper.pharm.ui.common.runVmTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class ImportFormViewModelTest {

    private fun drug(
        id: String = "d1",
        name: String = "Paracetamol",
        sellPrice: Money = Money(5.0),
        costPrice: Money = Money(2.0),
    ) = Drug(
        id = id,
        name = name,
        genericName = null,
        type = null,
        strength = null,
        barcode = null,
        sellPrice = sellPrice,
        costPrice = costPrice,
        stock = Quantity(0),
        minStock = Quantity(0),
        unit = "เม็ด",
        regNo = null,
    )

    private fun draftOrder(
        id: String = "po1",
        supplier: String = "ACME",
        status: PurchaseOrderStatus = PurchaseOrderStatus.Draft,
    ) = PurchaseOrder(
        id = id,
        docNo = "PO-$id",
        supplier = supplier,
        invoiceNo = "INV-1",
        receiveDate = kotlinx.datetime.LocalDate.parse("2026-05-14"),
        items = listOf(
            PurchaseOrderItem(
                drugId = "d1",
                drugName = "Paracetamol",
                lotNumber = "L-1",
                expiryDate = kotlinx.datetime.LocalDate.parse("2027-12-31"),
                qty = Quantity(100),
                costPrice = Money(2.0),
                sellPrice = Money(5.0),
            ),
        ),
        itemCount = 1,
        totalCost = Money(200.0),
        status = status,
        notes = "",
        createdAt = kotlinx.datetime.LocalDateTime.parse("2026-05-01T00:00:00"),
        confirmedAt = null,
    )

    private data class Bundle(
        val vm: ImportFormViewModel,
        val poRepo: FakePurchaseOrderRepository,
    )

    private fun newVm(
        dispatchers: AppDispatchers,
        poRepo: FakePurchaseOrderRepository = FakePurchaseOrderRepository(),
        drugRepo: FakeDrugRepository = FakeDrugRepository(),
        supplierRepo: FakeSupplierRepository = FakeSupplierRepository(),
    ): Bundle {
        val vm = ImportFormViewModel(
            getPurchaseOrder = GetPurchaseOrderUseCase(poRepo, dispatchers),
            addPurchaseOrder = AddPurchaseOrderUseCase(poRepo, dispatchers),
            updatePurchaseOrder = UpdatePurchaseOrderUseCase(poRepo, dispatchers),
            getDrugs = GetDrugsUseCase(drugRepo, dispatchers),
            getSuppliers = GetSuppliersUseCase(supplierRepo, dispatchers),
        )
        return Bundle(vm, poRepo)
    }

    @Test
    fun add_mode_starts_with_empty_form_and_loads_drugs_suppliers() = runVmTest { dispatchers ->
        val (vm, _) = newVm(
            dispatchers,
            drugRepo = FakeDrugRepository(seed = listOf(drug())),
        )
        vm.init(ImportFormMode.Add)
        advanceUntilIdle()
        val s = vm.state.value
        assertTrue(s.mode is ImportFormMode.Add)
        assertFalse(s.isEdit)
        assertEquals("", s.form.supplier)
        assertTrue(s.form.items.isEmpty())
        assertEquals(1, s.drugs.size)
        assertFalse(s.canSubmit)
    }

    @Test
    fun edit_mode_draft_hydrates_form_writable() = runVmTest { dispatchers ->
        val (vm, _) = newVm(
            dispatchers,
            poRepo = FakePurchaseOrderRepository(seed = mapOf("po1" to draftOrder())),
        )
        vm.init(ImportFormMode.Edit("po1"))
        advanceUntilIdle()
        val s = vm.state.value
        assertEquals("ACME", s.form.supplier)
        assertEquals("INV-1", s.form.invoiceNo)
        assertEquals(1, s.form.items.size)
        assertEquals("L-1", s.form.items[0].lotNumber)
        assertEquals("100", s.form.items[0].qty)
        assertFalse(s.readOnly)
        assertTrue(s.isEdit)
    }

    @Test
    fun edit_mode_confirmed_marks_readOnly() = runVmTest { dispatchers ->
        val (vm, _) = newVm(
            dispatchers,
            poRepo = FakePurchaseOrderRepository(
                seed = mapOf("po2" to draftOrder(id = "po2", status = PurchaseOrderStatus.Confirmed)),
            ),
        )
        vm.init(ImportFormMode.Edit("po2"))
        advanceUntilIdle()
        assertTrue(vm.state.value.readOnly)
        assertFalse(vm.state.value.canSubmit)
    }

    @Test
    fun edit_mode_not_found_surfaces_error() = runVmTest { dispatchers ->
        val (vm, _) = newVm(dispatchers)
        vm.init(ImportFormMode.Edit("missing"))
        advanceUntilIdle()
        assertNotNull(vm.state.value.errorState)
        assertFalse(vm.state.value.loading)
    }

    @Test
    fun addLine_then_removeLine_by_index() = runVmTest { dispatchers ->
        val (vm, _) = newVm(dispatchers)
        vm.init(ImportFormMode.Add)
        advanceUntilIdle()
        vm.addLine()
        vm.addLine()
        assertEquals(2, vm.state.value.form.items.size)
        vm.onLineLotNumber(0, "A")
        vm.onLineLotNumber(1, "B")
        vm.removeLine(0)
        val items = vm.state.value.form.items
        assertEquals(1, items.size)
        assertEquals("B", items[0].lotNumber)
    }

    @Test
    fun onLineDrug_seeds_cost_and_sell_when_blank() = runVmTest { dispatchers ->
        val (vm, _) = newVm(dispatchers)
        vm.init(ImportFormMode.Add)
        advanceUntilIdle()
        vm.addLine()
        vm.onLineDrug(0, drug(sellPrice = Money(7.0), costPrice = Money(3.5)))
        val line = vm.state.value.form.items[0]
        assertEquals("d1", line.drugId)
        assertEquals("Paracetamol", line.drugName)
        assertEquals("3.5", line.costPrice)
        assertEquals("7", line.sellPrice)
    }

    @Test
    fun onLineDrug_preserves_user_entered_cost_and_sell() = runVmTest { dispatchers ->
        val (vm, _) = newVm(dispatchers)
        vm.init(ImportFormMode.Add)
        advanceUntilIdle()
        vm.addLine()
        vm.onLineCost(0, "9.99")
        vm.onLineSell(0, "12")
        vm.onLineDrug(0, drug(costPrice = Money(3.5), sellPrice = Money(7.0)))
        val line = vm.state.value.form.items[0]
        assertEquals("9.99", line.costPrice)
        assertEquals("12", line.sellPrice)
    }

    @Test
    fun onLineQty_strips_non_digit_chars() = runVmTest { dispatchers ->
        val (vm, _) = newVm(dispatchers)
        vm.init(ImportFormMode.Add)
        advanceUntilIdle()
        vm.addLine()
        vm.onLineQty(0, "12.5abc")
        assertEquals("125", vm.state.value.form.items[0].qty)
    }

    @Test
    fun onLineCost_filterMoney_keeps_single_dot() = runVmTest { dispatchers ->
        val (vm, _) = newVm(dispatchers)
        vm.init(ImportFormMode.Add)
        advanceUntilIdle()
        vm.addLine()
        vm.onLineCost(0, "1.2.3.4abc")
        assertEquals("1.234", vm.state.value.form.items[0].costPrice)
    }

    @Test
    fun canSubmit_requires_supplier_and_valid_line() = runVmTest { dispatchers ->
        val (vm, _) = newVm(dispatchers)
        vm.init(ImportFormMode.Add)
        advanceUntilIdle()
        assertFalse(vm.state.value.canSubmit)
        vm.onSupplier("ACME")
        vm.addLine()
        vm.onLineDrug(0, drug())
        vm.onLineLotNumber(0, "L-1")
        vm.onLineExpiry(0, "2027-12-31")
        vm.onLineQty(0, "10")
        vm.onLineCost(0, "2")
        assertTrue(vm.state.value.canSubmit)
    }

    @Test
    fun submit_add_happy_path_sends_AddPurchaseOrderParam_marks_saved() = runVmTest { dispatchers ->
        val (vm, poRepo) = newVm(dispatchers)
        vm.init(ImportFormMode.Add)
        advanceUntilIdle()
        vm.onSupplier("  ACME  ")
        vm.onInvoiceNo("INV-001")
        vm.onReceiveDate("2026-05-14")
        vm.onNotes("note")
        vm.addLine()
        vm.onLineDrug(0, drug())
        vm.onLineLotNumber(0, "L-1")
        vm.onLineExpiry(0, "2027-12-31")
        vm.onLineQty(0, "50")
        vm.onLineCost(0, "2.50")
        vm.submit()
        advanceUntilIdle()
        val p = poRepo.lastAdd
        assertNotNull(p)
        assertEquals("ACME", p.supplier)
        assertEquals("INV-001", p.invoiceNo)
        assertEquals(1, p.items.size)
        assertEquals(Quantity(50), p.items[0].qty)
        assertEquals(Money(2.50), p.items[0].costPrice)
        assertTrue(vm.state.value.saved)
    }

    @Test
    fun submit_edit_happy_path_uses_update_with_id() = runVmTest { dispatchers ->
        val (vm, poRepo) = newVm(
            dispatchers,
            poRepo = FakePurchaseOrderRepository(seed = mapOf("po99" to draftOrder(id = "po99"))),
        )
        vm.init(ImportFormMode.Edit("po99"))
        advanceUntilIdle()
        vm.onSupplier("Renamed")
        vm.submit()
        advanceUntilIdle()
        val p = poRepo.lastUpdate
        assertNotNull(p)
        assertEquals("po99", p.id)
        assertEquals("Renamed", p.supplier)
        assertTrue(vm.state.value.saved)
    }

    @Test
    fun submit_failure_surfaces_error_keeps_saved_false() = runVmTest { dispatchers ->
        val (vm, _) = newVm(
            dispatchers,
            poRepo = FakePurchaseOrderRepository(addThrowsOn = "BAD"),
        )
        vm.init(ImportFormMode.Add)
        advanceUntilIdle()
        vm.onSupplier("BAD")
        vm.addLine()
        vm.onLineDrug(0, drug())
        vm.onLineLotNumber(0, "L-1")
        vm.onLineExpiry(0, "2027-12-31")
        vm.onLineQty(0, "10")
        vm.onLineCost(0, "2")
        vm.submit()
        advanceUntilIdle()
        assertFalse(vm.state.value.saved)
        assertNotNull(vm.state.value.errorState)
    }

    @Test
    fun dismissError_clears_error() = runVmTest { dispatchers ->
        val (vm, _) = newVm(dispatchers)
        vm.init(ImportFormMode.Edit("missing"))
        advanceUntilIdle()
        assertNotNull(vm.state.value.errorState)
        vm.dismissError()
        assertNull(vm.state.value.errorState)
    }
}
