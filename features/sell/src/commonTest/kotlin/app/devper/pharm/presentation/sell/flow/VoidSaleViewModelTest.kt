package app.devper.pharm.presentation.sell.flow

import app.devper.pharm.common.AppDispatchers
import app.devper.pharm.domain.event.StockChangeBus
import app.devper.pharm.domain.model.Sale
import app.devper.pharm.domain.repository.FakeCartRepository
import app.devper.pharm.domain.repository.FakeSaleRepository
import app.devper.pharm.domain.usecase.DismissReceiptUseCase
import app.devper.pharm.domain.usecase.VoidSaleUseCase
import app.devper.pharm.ui.common.runVmTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class VoidSaleViewModelTest {

    private data class Bundle(
        val vm: VoidSaleViewModel,
        val cart: FakeCartRepository,
        val sales: FakeSaleRepository,
        val bus: StockChangeBus,
    )

    private fun newVm(
        dispatchers: AppDispatchers,
        cart: FakeCartRepository = FakeCartRepository(

            initialReceipt = Sale("s1", "INV-001", 10.0, 0.0, 0.0, emptyList()),
        ),
        sales: FakeSaleRepository = FakeSaleRepository(),
        bus: StockChangeBus = StockChangeBus(),
    ): Bundle {
        val vm = VoidSaleViewModel(
            voidSale = VoidSaleUseCase(sales, dispatchers),
            dismissReceiptUseCase = DismissReceiptUseCase(cart),
        )
        return Bundle(vm, cart, sales, bus)
    }

    @Test
    fun openSheet_closeSheet_toggle_local_state() = runVmTest { dispatchers ->
        val (vm) = newVm(dispatchers)
        advanceUntilIdle()
        assertFalse(vm.state.value.sheetOpen)
        vm.openSheet()
        assertTrue(vm.state.value.sheetOpen)
        vm.closeSheet()
        assertFalse(vm.state.value.sheetOpen)
    }

    @Test
    fun confirm_happy_path_voids_and_dismisses_receipt() = runVmTest { dispatchers ->
        val (vm, cart, sales) = newVm(dispatchers)
        advanceUntilIdle()
        vm.openSheet()
        vm.confirm(saleId = "s1", reason = "ลูกค้าคืน")
        advanceUntilIdle()

        assertNotNull(sales.lastVoid)
        assertEquals("s1", sales.lastVoid?.saleId)
        assertEquals("ลูกค้าคืน", sales.lastVoid?.reason)

        assertTrue(cart.dismissReceiptCalled)
        assertNull(cart.state.value.lastReceipt)

        assertFalse(vm.state.value.sheetOpen)
        assertFalse(vm.state.value.submitting)
        assertNull(vm.state.value.error)
    }

    @Test
    fun confirm_with_blank_saleId_short_circuits_with_soft_error() = runVmTest { dispatchers ->
        val (vm, _, sales) = newVm(dispatchers)
        advanceUntilIdle()
        vm.openSheet()
        vm.confirm(saleId = "   ", reason = "ลูกค้าคืน")
        advanceUntilIdle()

        assertNull(sales.lastVoid)

        assertFalse(vm.state.value.sheetOpen)
        assertEquals("ไม่สามารถยกเลิกบิลนี้: ไม่พบรหัสบิล", vm.state.value.error)
        assertFalse(vm.state.value.submitting)
    }

    @Test
    fun confirm_with_blank_reason_routes_to_failure_via_use_case_validation() = runVmTest { dispatchers ->

        val (vm, cart, sales) = newVm(dispatchers)
        advanceUntilIdle()
        vm.openSheet()
        vm.confirm(saleId = "s1", reason = "  ")
        advanceUntilIdle()

        assertNull(sales.lastVoid)

        assertFalse(cart.dismissReceiptCalled)

        assertFalse(vm.state.value.sheetOpen)
        assertNotNull(vm.state.value.error)
        assertEquals("กรุณาระบุเหตุผลการยกเลิก", vm.state.value.error)
        assertFalse(vm.state.value.submitting)
    }

    @Test
    fun confirm_backend_failure_surfaces_error_and_keeps_receipt() = runVmTest { dispatchers ->
        val sales = FakeSaleRepository(voidThrows = RuntimeException("backend 500"))
        val (vm, cart) = newVm(dispatchers, sales = sales)
        advanceUntilIdle()
        vm.openSheet()
        vm.confirm(saleId = "s1", reason = "ลูกค้าคืน")
        advanceUntilIdle()

        assertNotNull(cart.state.value.lastReceipt)
        assertFalse(cart.dismissReceiptCalled)
        assertEquals("backend 500", vm.state.value.error)
        assertFalse(vm.state.value.sheetOpen)
        assertFalse(vm.state.value.submitting)

        assertNull(sales.lastVoid)
    }

    @Test
    fun dismissError_clears_error() = runVmTest { dispatchers ->
        val (vm) = newVm(dispatchers)
        advanceUntilIdle()
        vm.confirm(saleId = "", reason = "x")
        advanceUntilIdle()
        assertNotNull(vm.state.value.error)
        vm.dismissError()
        assertNull(vm.state.value.error)
    }
}
