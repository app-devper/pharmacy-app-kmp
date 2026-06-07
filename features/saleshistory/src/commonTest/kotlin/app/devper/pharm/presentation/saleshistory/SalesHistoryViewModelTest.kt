package app.devper.pharm.presentation.saleshistory

import app.devper.pharm.common.AppDispatchers
import app.devper.pharm.domain.model.SaleItemSnapshot
import app.devper.pharm.domain.model.SaleSummary
import app.devper.pharm.domain.repository.FakeSaleHistoryRepository
import app.devper.pharm.domain.usecase.GetSaleHistoryUseCase
import app.devper.pharm.domain.usecase.GetSaleItemsUseCase
import app.devper.pharm.domain.usecase.SubmitSaleReturnUseCase
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
class SalesHistoryViewModelTest {

    private fun summary(id: String = "s1", billNo: String = "INV-1") = SaleSummary(
        id = id,
        billNo = billNo,
        customerName = "Walk-in",
        total = 100.0,
        discount = 0.0,
        soldAt = "2026-05-14T10:00:00Z",
        voided = false,
    )

    private fun item(id: String, qty: Int = 2, unitFactor: Int = 1) = SaleItemSnapshot(
        id = id,
        drugId = "d-$id",
        drugName = "Drug $id",
        qty = qty,
        price = 25.0,
        originalPrice = 25.0,
        itemDiscount = 0.0,
        unit = "เม็ด",
        unitFactor = unitFactor,
        priceTier = "",
    )

    private data class Bundle(
        val vm: SalesHistoryViewModel,
        val repo: FakeSaleHistoryRepository,
    )

    private fun newVm(
        dispatchers: AppDispatchers,
        repo: FakeSaleHistoryRepository = FakeSaleHistoryRepository(),
    ): Bundle {
        val vm = SalesHistoryViewModel(
            getHistory = GetSaleHistoryUseCase(repo, dispatchers),
            getItems = GetSaleItemsUseCase(repo, dispatchers),
            submitReturn = SubmitSaleReturnUseCase(repo, dispatchers),
            timeZoneProvider = app.devper.pharm.domain.observer.testTimeZoneProvider(),
        )
        return Bundle(vm, repo)
    }

    @Test
    fun init_loads_list_into_state() = runVmTest { dispatchers ->
        val (vm, repo) = newVm(
            dispatchers,
            FakeSaleHistoryRepository(seed = listOf(summary("a"), summary("b"))),
        )
        advanceUntilIdle()
        assertEquals(2, vm.state.value.sales.size)
        assertFalse(vm.state.value.loading)
        assertEquals(1, repo.listCallCount)
    }

    @Test
    fun applyFilter_routes_filter_param_to_repo() = runVmTest { dispatchers ->
        val (vm, repo) = newVm(dispatchers)
        advanceUntilIdle()
        vm.onFromChange("2026-05-01")
        vm.onToChange("2026-05-31")
        vm.onQueryChange("INV-X")
        vm.applyFilter()
        advanceUntilIdle()
        assertEquals("2026-05-01", repo.lastListFilter?.from)
        assertEquals("2026-05-31", repo.lastListFilter?.to)
        assertEquals("INV-X", repo.lastListFilter?.query)
    }

    @Test
    fun applyFilter_strips_blank_strings_to_null() = runVmTest { dispatchers ->
        val (vm, repo) = newVm(dispatchers)
        advanceUntilIdle()
        vm.onFromChange("  ")
        vm.applyFilter()
        advanceUntilIdle()
        assertNull(repo.lastListFilter?.from)
        assertNull(repo.lastListFilter?.to)
        assertNull(repo.lastListFilter?.query)
    }

    @Test
    fun loadList_failure_surfaces_error_clears_loading() = runVmTest { dispatchers ->
        val (vm, _) = newVm(dispatchers, FakeSaleHistoryRepository(listThrows = true))
        advanceUntilIdle()
        assertFalse(vm.state.value.loading)
        assertNotNull(vm.state.value.error)
        assertTrue(vm.state.value.sales.isEmpty())
    }

    @Test
    fun onSelectSale_loads_items_with_merged_returns() = runVmTest { dispatchers ->
        val sale = summary("s1")
        val (vm, _) = newVm(
            dispatchers,
            FakeSaleHistoryRepository(
                seed = listOf(sale),
                itemsBySale = mapOf("s1" to listOf(item("i1", qty = 5), item("i2", qty = 3))),
                returnsBySale = mapOf("s1" to mapOf("i1" to 2)),
            ),
        )
        advanceUntilIdle()
        vm.onSelectSale(sale)
        advanceUntilIdle()
        val s = vm.state.value
        assertEquals("s1", s.selected?.id)
        assertEquals(2, s.items.size)
        assertEquals(2, s.items.first { it.id == "i1" }.returnedQty)
        assertEquals(0, s.items.first { it.id == "i2" }.returnedQty)
        assertFalse(s.itemsLoading)
    }

    @Test
    fun onSelectSale_returns_failure_falls_back_to_zero_returned() = runVmTest { dispatchers ->
        val sale = summary("s1")
        val (vm, _) = newVm(
            dispatchers,
            FakeSaleHistoryRepository(
                seed = listOf(sale),
                itemsBySale = mapOf("s1" to listOf(item("i1", qty = 5))),
                returnsThrows = true,
            ),
        )
        advanceUntilIdle()
        vm.onSelectSale(sale)
        advanceUntilIdle()
        assertEquals(0, vm.state.value.items.single().returnedQty)
        assertFalse(vm.state.value.itemsLoading)
        assertNull(vm.state.value.error)
    }

    @Test
    fun onSelectSale_ignores_stale_results_when_selection_changes_mid_flight() = runVmTest { dispatchers ->
        val saleA = summary(id = "sA", billNo = "A")
        val saleB = summary(id = "sB", billNo = "B")
        val itemA = item("iA", qty = 5).copy(drugName = "DrugA")
        val itemB = item("iB", qty = 7).copy(drugName = "DrugB")
        val (vm, _) = newVm(
            dispatchers,
            FakeSaleHistoryRepository(
                seed = listOf(saleA, saleB),
                itemsBySale = mapOf("sA" to listOf(itemA), "sB" to listOf(itemB)),
            ),
        )
        advanceUntilIdle()

        vm.onSelectSale(saleA)
        vm.onSelectSale(saleB)
        advanceUntilIdle()

        val s = vm.state.value
        assertEquals("sB", s.selected?.id)
        assertEquals(1, s.items.size)
        assertEquals("DrugB", s.items.single().drugName)
        assertFalse(s.itemsLoading)
    }

    @Test
    fun onClearSelection_resets_detail_state() = runVmTest { dispatchers ->
        val sale = summary("s1")
        val (vm, _) = newVm(
            dispatchers,
            FakeSaleHistoryRepository(
                seed = listOf(sale),
                itemsBySale = mapOf("s1" to listOf(item("i1"))),
            ),
        )
        advanceUntilIdle()
        vm.onSelectSale(sale)
        advanceUntilIdle()
        vm.onOpenReturnSheet()
        vm.onClearSelection()
        val s = vm.state.value
        assertNull(s.selected)
        assertTrue(s.items.isEmpty())
        assertFalse(s.returnSheetOpen)
        assertTrue(s.returnDraft.isEmpty())
    }

    @Test
    fun onOpenReturnSheet_seeds_draft_with_zero_per_item() = runVmTest { dispatchers ->
        val sale = summary("s1")
        val (vm, _) = newVm(
            dispatchers,
            FakeSaleHistoryRepository(
                seed = listOf(sale),
                itemsBySale = mapOf("s1" to listOf(item("i1"), item("i2"))),
            ),
        )
        advanceUntilIdle()
        vm.onSelectSale(sale)
        advanceUntilIdle()
        vm.onOpenReturnSheet()
        val draft = vm.state.value.returnDraft
        assertEquals(setOf("i1", "i2"), draft.keys)
        assertTrue(draft.values.all { it == 0 })
        assertTrue(vm.state.value.returnSheetOpen)
    }

    @Test
    fun onReturnLineQtyChange_converts_display_qty_to_base() = runVmTest { dispatchers ->
        val sale = summary("s1")
        val (vm, _) = newVm(
            dispatchers,
            FakeSaleHistoryRepository(
                seed = listOf(sale),
                itemsBySale = mapOf("s1" to listOf(item("i1", qty = 20, unitFactor = 10))),
            ),
        )
        advanceUntilIdle()
        vm.onSelectSale(sale)
        advanceUntilIdle()
        vm.onOpenReturnSheet()
        vm.onReturnLineQtyChange("i1", displayQty = 1)
        assertEquals(10, vm.state.value.returnDraft["i1"])
    }

    @Test
    fun confirmReturn_happy_path_submits_filtered_lines_and_reloads_items() = runVmTest { dispatchers ->
        val sale = summary("s1")
        val (vm, repo) = newVm(
            dispatchers,
            FakeSaleHistoryRepository(
                seed = listOf(sale),
                itemsBySale = mapOf("s1" to listOf(item("i1", qty = 5), item("i2", qty = 3))),
            ),
        )
        advanceUntilIdle()
        vm.onSelectSale(sale)
        advanceUntilIdle()
        vm.onOpenReturnSheet()
        vm.onReturnReasonChange("ลูกค้าคืน")
        vm.onReturnLineQtyChange("i1", displayQty = 2)
        val itemsCallsBefore = repo.itemsCallCount
        vm.confirmReturn()
        advanceUntilIdle()
        val submitted = repo.lastSubmitReturn
        assertNotNull(submitted)
        assertEquals("s1", submitted.saleId)
        assertEquals("ลูกค้าคืน", submitted.reason)
        assertEquals(1, submitted.items.size)
        assertEquals("i1", submitted.items[0].saleItemId)
        assertEquals(2, submitted.items[0].qty)
        assertFalse(vm.state.value.returnSheetOpen)
        assertFalse(vm.state.value.submittingReturn)
        assertTrue(repo.itemsCallCount > itemsCallsBefore)
    }

    @Test
    fun confirmReturn_no_op_when_no_selected_sale() = runVmTest { dispatchers ->
        val (vm, repo) = newVm(dispatchers)
        advanceUntilIdle()
        vm.confirmReturn()
        advanceUntilIdle()
        assertNull(repo.lastSubmitReturn)
        assertFalse(vm.state.value.submittingReturn)
    }

    @Test
    fun confirmReturn_failure_surfaces_error_keeps_sheet_open() = runVmTest { dispatchers ->
        val sale = summary("s1")
        val (vm, _) = newVm(
            dispatchers,
            FakeSaleHistoryRepository(
                seed = listOf(sale),
                itemsBySale = mapOf("s1" to listOf(item("i1", qty = 5))),
                submitThrowsOn = "s1",
            ),
        )
        advanceUntilIdle()
        vm.onSelectSale(sale)
        advanceUntilIdle()
        vm.onOpenReturnSheet()
        vm.onReturnLineQtyChange("i1", displayQty = 2)
        vm.confirmReturn()
        advanceUntilIdle()
        assertNotNull(vm.state.value.error)
        assertFalse(vm.state.value.submittingReturn)
    }

    @Test
    fun dismissError_clears_error() = runVmTest { dispatchers ->
        val (vm, _) = newVm(dispatchers, FakeSaleHistoryRepository(listThrows = true))
        advanceUntilIdle()
        assertNotNull(vm.state.value.error)
        vm.dismissError()
        assertNull(vm.state.value.error)
    }
}
