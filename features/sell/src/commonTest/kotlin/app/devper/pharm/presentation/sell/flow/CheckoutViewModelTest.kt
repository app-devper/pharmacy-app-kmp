package app.devper.pharm.presentation.sell.flow

import app.devper.pharm.presentation.sell.exception.CheckoutUiStateError

import app.devper.pharm.common.value.Money
import app.devper.pharm.common.value.Quantity

import app.devper.pharm.common.AppDispatchers
import app.devper.pharm.common.print.ReceiptPrinter
import app.devper.pharm.common.print.ReceiptTemplate
import app.devper.pharm.domain.event.StockChangeBus
import app.devper.pharm.domain.model.CartLine
import app.devper.pharm.domain.model.Customer
import app.devper.pharm.domain.model.Drug
import app.devper.pharm.domain.model.KyCaptureFields
import app.devper.pharm.domain.model.Sale
import app.devper.pharm.domain.model.Settings
import app.devper.pharm.domain.observer.CartStateProvider
import app.devper.pharm.domain.observer.SettingsProvider
import app.devper.pharm.domain.repository.FakeCartRepository
import app.devper.pharm.domain.repository.FakeKyRepository
import app.devper.pharm.domain.repository.FakeOfflineSaleQueue
import app.devper.pharm.domain.repository.FakeSaleRepository
import app.devper.pharm.domain.repository.FakeSettingsRepository
import app.devper.pharm.domain.usecase.CheckoutUseCase
import app.devper.pharm.domain.usecase.ClearCartUseCase
import app.devper.pharm.domain.usecase.DismissReceiptUseCase
import app.devper.pharm.domain.usecase.EnqueueOfflineSaleUseCase
import app.devper.pharm.domain.usecase.SubmitKyFormsUseCase
import app.devper.pharm.ui.common.runVmTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

private class StubReceiptPrinter(private val result: Boolean = true) : ReceiptPrinter {
    override fun print(template: ReceiptTemplate): Boolean = result
}

@OptIn(ExperimentalCoroutinesApi::class)
class CheckoutViewModelTest {

    private fun drug(
        id: String = "d1",
        name: String = "Paracetamol",
        stock: Quantity = Quantity(100),
        price: Money = Money(5.0),
        reportTypes: List<String> = emptyList(),
    ) = Drug(
        id = id,
        name = name,
        genericName = null,
        type = null,
        strength = null,
        barcode = null,
        sellPrice = price,
        costPrice = Money(0.0),
        stock = stock,
        minStock = Quantity(0),
        unit = "เม็ด",
        regNo = "REG-001",
        reportTypes = reportTypes,
    )

    private fun line(drug: Drug = drug(), qty: Int = 1, discount: Money = Money.Zero) =
        CartLine(drug = drug, qty = qty, discount = discount)

    private fun customer(id: String = "c1") = Customer(id, "John", null, "", null)

    private data class Bundle(
        val vm: CheckoutViewModel,
        val cart: FakeCartRepository,
        val sales: FakeSaleRepository,
        val ky: FakeKyRepository,
        val offline: FakeOfflineSaleQueue,
        val bus: StockChangeBus,
    )

    private fun newVm(
        dispatchers: AppDispatchers,
        cart: FakeCartRepository = FakeCartRepository(),
        sales: FakeSaleRepository = FakeSaleRepository(),
        ky: FakeKyRepository = FakeKyRepository(),
        offline: FakeOfflineSaleQueue = FakeOfflineSaleQueue(),
        settings: FakeSettingsRepository = FakeSettingsRepository(),
        bus: StockChangeBus = StockChangeBus(),
    ): Bundle {
        val vm = CheckoutViewModel(
            cartState = CartStateProvider(cart),
            settings = SettingsProvider(settings),
            timeZoneProvider = app.devper.pharm.domain.observer.testTimeZoneProvider(),
            checkout = CheckoutUseCase(cart, sales, dispatchers),
            clearCart = ClearCartUseCase(cart),
            dismissReceiptUseCase = DismissReceiptUseCase(cart),
            submitKyForms = SubmitKyFormsUseCase(ky, dispatchers),
            enqueueOfflineSale = EnqueueOfflineSaleUseCase(offline),
            receiptPrinter = StubReceiptPrinter(),
        )
        return Bundle(vm, cart, sales, ky, offline, bus)
    }

    @Test
    fun canCheckout_false_when_cart_is_empty() = runVmTest { dispatchers ->
        val (vm) = newVm(dispatchers)
        advanceUntilIdle()
        assertTrue(vm.state.value.cartIsEmpty)
        assertFalse(vm.state.value.canCheckout)
    }

    @Test
    fun canCheckout_false_when_tender_insufficient() = runVmTest { dispatchers ->
        val (vm) = newVm(
            dispatchers,
            FakeCartRepository(initialItems = listOf(line(qty = 2)), initialReceived = "5"),

        )
        advanceUntilIdle()
        assertFalse(vm.state.value.cartIsEmpty)
        assertFalse(vm.state.value.tenderOk)
        assertFalse(vm.state.value.canCheckout)
    }

    @Test
    fun canCheckout_true_when_cart_non_empty_and_tender_ok() = runVmTest { dispatchers ->
        val (vm) = newVm(
            dispatchers,
            FakeCartRepository(initialItems = listOf(line()), initialReceived = "100"),
        )
        advanceUntilIdle()
        assertFalse(vm.state.value.cartIsEmpty)
        assertTrue(vm.state.value.tenderOk)
        assertTrue(vm.state.value.canCheckout)
    }

    @Test
    fun submit_no_op_when_canCheckout_is_false() = runVmTest { dispatchers ->
        val (vm, _, sales) = newVm(dispatchers)
        advanceUntilIdle()
        vm.submit()
        advanceUntilIdle()
        assertNull(sales.lastCheckout)
        assertFalse(vm.state.value.checkingOut)
    }

    @Test
    fun submit_happy_path_no_ky_commits_sale() = runVmTest { dispatchers ->
        val cart = FakeCartRepository(
            initialItems = listOf(line(qty = 2)),
            initialCustomer = customer(),
            initialReceived = "100",
        )
        val (vm, _, sales, _, _) = newVm(dispatchers, cart = cart)
        advanceUntilIdle()
        vm.submit()
        advanceUntilIdle()

        assertNotNull(sales.lastCheckout)
        assertEquals(1, sales.lastCheckout!!.items.size)
        assertEquals(2, sales.lastCheckout!!.items[0].qty)

        assertNotNull(cart.lastCommitReceipt)
        assertTrue(cart.state.value.active.items.isEmpty())

        assertFalse(vm.state.value.checkingOut)
        assertNull(vm.state.value.errorState)
    }

    @Test
    fun submit_serializes_before_post_so_offline_replay_has_payload() = runVmTest { dispatchers ->
        val (vm, _, sales) = newVm(
            dispatchers,
            FakeCartRepository(initialItems = listOf(line()), initialReceived = "100"),
        )
        advanceUntilIdle()
        vm.submit()
        advanceUntilIdle()

        assertTrue(sales.serializeCalls >= 1)
    }

    @Test
    fun submit_with_ky_required_opens_capture_sheet_without_posting() = runVmTest { dispatchers ->
        val kyDrug = drug(id = "kd", reportTypes = listOf("ky10"))
        val (vm, _, sales) = newVm(
            dispatchers,
            FakeCartRepository(initialItems = listOf(line(drug = kyDrug)), initialReceived = "100"),
        )
        advanceUntilIdle()
        vm.submit()
        advanceUntilIdle()

        val required = vm.state.value.kyCapturePending
        assertNotNull(required)
        assertTrue(required.needsKy10)

        assertNull(sales.lastCheckout)
    }

    @Test
    fun confirmKyCapture_runs_checkout_and_fans_out_ky_forms() = runVmTest { dispatchers ->
        val kyDrug = drug(id = "kd", reportTypes = listOf("ky10", "ky11"))
        val (vm, _, sales, ky) = newVm(
            dispatchers,
            FakeCartRepository(initialItems = listOf(line(drug = kyDrug)), initialReceived = "100"),
        )
        advanceUntilIdle()
        vm.submit()
        advanceUntilIdle()
        vm.confirmKyCapture(
            KyCaptureFields(
                ky10BuyerName = "Buyer",
                ky10BuyerAddress = "Addr",
                ky11BuyerName = "Buyer",
                ky11Pharmacist = "Pharm",
            ),
        )
        advanceUntilIdle()

        assertNotNull(sales.lastCheckout)

        assertEquals(1, ky.ky10Submissions.size)
        assertEquals(1, ky.ky11Submissions.size)
        assertEquals(0, ky.ky12Submissions.size)
        assertEquals("Buyer", ky.ky10Submissions[0].buyerName)
        assertEquals("Pharm", ky.ky11Submissions[0].pharmacist)

        assertNull(vm.state.value.kyCapturePending)
    }

    @Test
    fun requestSkipKy_opens_confirm_without_skipping() = runVmTest { dispatchers ->
        val kyDrug = drug(id = "kd", reportTypes = listOf("ky10"))
        val (vm, _, sales, ky) = newVm(
            dispatchers,
            FakeCartRepository(initialItems = listOf(line(drug = kyDrug)), initialReceived = "100"),
        )
        advanceUntilIdle()
        vm.submit()
        advanceUntilIdle()
        vm.requestSkipKy()
        advanceUntilIdle()

        assertTrue(vm.state.value.showSkipKyConfirm)
        assertNotNull(vm.state.value.kyCapturePending)
        assertNull(sales.lastCheckout)
        assertEquals(0, ky.ky10Submissions.size)
    }

    @Test
    fun cancelSkipKy_clears_confirm_without_skipping() = runVmTest { dispatchers ->
        val kyDrug = drug(id = "kd", reportTypes = listOf("ky10"))
        val (vm, _, sales) = newVm(
            dispatchers,
            FakeCartRepository(initialItems = listOf(line(drug = kyDrug)), initialReceived = "100"),
        )
        advanceUntilIdle()
        vm.submit()
        advanceUntilIdle()
        vm.requestSkipKy()
        advanceUntilIdle()
        vm.cancelSkipKy()
        advanceUntilIdle()

        assertFalse(vm.state.value.showSkipKyConfirm)
        assertNotNull(vm.state.value.kyCapturePending)
        assertNull(sales.lastCheckout)
    }

    @Test
    fun confirmSkipKy_runs_checkout_with_audit_flag_and_no_ky_fan_out() = runVmTest { dispatchers ->
        val kyDrug = drug(id = "kd", reportTypes = listOf("ky10"))
        val (vm, _, sales, ky) = newVm(
            dispatchers,
            FakeCartRepository(initialItems = listOf(line(drug = kyDrug)), initialReceived = "100"),
        )
        advanceUntilIdle()
        vm.submit()
        advanceUntilIdle()
        vm.requestSkipKy()
        advanceUntilIdle()
        vm.confirmSkipKy()
        advanceUntilIdle()

        assertNotNull(sales.lastCheckout)
        assertEquals(true, sales.lastCheckout!!.kySkippedByCashier)
        assertEquals(0, ky.ky10Submissions.size)
        assertNull(vm.state.value.kyCapturePending)
        assertFalse(vm.state.value.showSkipKyConfirm)
    }

    @Test
    fun confirmKyCapture_does_not_mark_audit_flag() = runVmTest { dispatchers ->
        val kyDrug = drug(id = "kd", reportTypes = listOf("ky10"))
        val (vm, _, sales) = newVm(
            dispatchers,
            FakeCartRepository(initialItems = listOf(line(drug = kyDrug)), initialReceived = "100"),
        )
        advanceUntilIdle()
        vm.submit()
        advanceUntilIdle()
        vm.confirmKyCapture(
            KyCaptureFields(ky10BuyerName = "B", ky10BuyerAddress = "A"),
        )
        advanceUntilIdle()

        assertNotNull(sales.lastCheckout)
        assertEquals(false, sales.lastCheckout!!.kySkippedByCashier)
    }

    @Test
    fun dismissKyCapture_aborts_without_posting() = runVmTest { dispatchers ->
        val kyDrug = drug(id = "kd", reportTypes = listOf("ky10"))
        val (vm, _, sales) = newVm(
            dispatchers,
            FakeCartRepository(initialItems = listOf(line(drug = kyDrug)), initialReceived = "100"),
        )
        advanceUntilIdle()
        vm.submit()
        advanceUntilIdle()
        vm.dismissKyCapture()
        advanceUntilIdle()
        assertNull(vm.state.value.kyCapturePending)
        assertNull(sales.lastCheckout)
    }

    @Test
    fun skip_auto_setting_bypasses_ky_capture_sheet() = runVmTest { dispatchers ->
        val kyDrug = drug(id = "kd", reportTypes = listOf("ky10"))
        val settings = FakeSettingsRepository(
            initialSettings = Settings(
                ky = app.devper.pharm.domain.model.KySettings(skipAuto = true),
            ),
        )
        val (vm, _, sales, ky) = newVm(
            dispatchers,
            cart = FakeCartRepository(initialItems = listOf(line(drug = kyDrug)), initialReceived = "100"),
            settings = settings,
        )
        advanceUntilIdle()
        vm.submit()
        advanceUntilIdle()

        assertNull(vm.state.value.kyCapturePending)
        assertNotNull(sales.lastCheckout)
        assertEquals(true, sales.lastCheckout!!.kySkippedByCashier)

        assertEquals(0, ky.ky10Submissions.size)
    }

    @Test
    fun submit_oversell_routes_to_confirm_sheet() = runVmTest { dispatchers ->
        val lowStock = drug(stock = Quantity(1))
        val (vm, _, sales) = newVm(
            dispatchers,
            FakeCartRepository(initialItems = listOf(line(drug = lowStock, qty = 3)), initialReceived = "100"),
        )
        advanceUntilIdle()
        vm.submit()
        advanceUntilIdle()
        val shortfalls = vm.state.value.oversellPending
        assertNotNull(shortfalls)
        assertEquals(1, shortfalls.size)
        assertEquals(3, shortfalls[0].asked)
        assertEquals(1, shortfalls[0].available)

        assertNull(sales.lastCheckout)
        assertFalse(vm.state.value.checkingOut)
    }

    @Test
    fun confirmOversell_reruns_with_allowOversell_flag() = runVmTest { dispatchers ->
        val lowStock = drug(stock = Quantity(1))
        val (vm, _, sales) = newVm(
            dispatchers,
            FakeCartRepository(initialItems = listOf(line(drug = lowStock, qty = 3)), initialReceived = "100"),
        )
        advanceUntilIdle()
        vm.submit()
        advanceUntilIdle()
        vm.confirmOversell()
        advanceUntilIdle()

        assertNotNull(sales.lastCheckout)
        assertEquals(true, sales.lastCheckout!!.items[0].allowOversell)
        assertNull(vm.state.value.oversellPending)
    }

    @Test
    fun dismissOversell_clears_pending_without_rerun() = runVmTest { dispatchers ->
        val lowStock = drug(stock = Quantity(1))
        val (vm, _, sales) = newVm(
            dispatchers,
            FakeCartRepository(initialItems = listOf(line(drug = lowStock, qty = 3)), initialReceived = "100"),
        )
        advanceUntilIdle()
        vm.submit()
        advanceUntilIdle()
        vm.dismissOversell()
        advanceUntilIdle()
        assertNull(vm.state.value.oversellPending)

        assertNull(sales.lastCheckout)
    }

    @Test
    fun network_failure_enqueues_offline_and_clears_cart() = runVmTest { dispatchers ->

        val sales = FakeSaleRepository(checkoutThrows = RuntimeException("Failed to connect to host"))
        val (vm, cart, _, _, offline) = newVm(
            dispatchers,
            cart = FakeCartRepository(initialItems = listOf(line()), initialReceived = "100"),
            sales = sales,
        )
        advanceUntilIdle()
        vm.submit()
        advanceUntilIdle()

        assertEquals(1, offline.pending.value.size)
        assertNotNull(offline.lastEnqueue)
        assertTrue(offline.lastEnqueue!!.payloadJson.contains("client_request_id"))
        assertTrue(cart.clearCalled)

        assertIs<CheckoutUiStateError.OfflineSaved>(vm.state.value.errorState)
        assertFalse(vm.state.value.checkingOut)
    }

    @Test
    fun non_network_failure_surfaces_error_without_enqueue() = runVmTest { dispatchers ->
        val sales = FakeSaleRepository(checkoutThrows = RuntimeException("validation: missing field"))
        val (vm, cart, _, _, offline) = newVm(
            dispatchers,
            cart = FakeCartRepository(initialItems = listOf(line()), initialReceived = "100"),
            sales = sales,
        )
        advanceUntilIdle()
        vm.submit()
        advanceUntilIdle()

        assertEquals(0, offline.pending.value.size)
        assertFalse(cart.clearCalled)
        assertIs<CheckoutUiStateError.CheckoutFailed>(vm.state.value.errorState)
        assertFalse(vm.state.value.checkingOut)
    }

    @Test
    fun dismissError_clears_error() = runVmTest { dispatchers ->
        val sales = FakeSaleRepository(checkoutThrows = RuntimeException("boom"))
        val (vm) = newVm(
            dispatchers,
            cart = FakeCartRepository(initialItems = listOf(line()), initialReceived = "100"),
            sales = sales,
        )
        advanceUntilIdle()
        vm.submit()
        advanceUntilIdle()
        assertNotNull(vm.state.value.errorState)
        vm.dismissError()
        assertNull(vm.state.value.errorState)
    }

    @Test
    fun dismissReceipt_invokes_dismissReceiptUseCase() = runVmTest { dispatchers ->

        val cart = FakeCartRepository(initialReceipt = Sale("s1", "INV-001", Money(10.0), Money(0.0), Money(0.0), emptyList()))
        val (vm) = newVm(dispatchers, cart = cart)
        advanceUntilIdle()

        assertNotNull(cart.state.value.lastReceipt)
        vm.dismissReceipt()
        advanceUntilIdle()
        assertTrue(cart.dismissReceiptCalled)
        assertNull(cart.state.value.lastReceipt)
    }

    @Test
    fun ky_row_failure_after_success_surfaces_warning_but_sale_stands() = runVmTest { dispatchers ->
        val kyDrug = drug(id = "kd", reportTypes = listOf("ky10"))
        val ky = FakeKyRepository(ky10Throws = true)
        val (vm, _, sales, _, _) = newVm(
            dispatchers,
            cart = FakeCartRepository(initialItems = listOf(line(drug = kyDrug)), initialReceived = "100"),
            ky = ky,
        )
        advanceUntilIdle()
        vm.submit()
        advanceUntilIdle()
        vm.confirmKyCapture(
            KyCaptureFields(ky10BuyerName = "B", ky10BuyerAddress = "A"),
        )
        advanceUntilIdle()

        assertNotNull(sales.lastCheckout)

        val kyErr = vm.state.value.errorState
        assertTrue(kyErr is CheckoutUiStateError.KyIncomplete || kyErr is CheckoutUiStateError.KyError)
    }

}
