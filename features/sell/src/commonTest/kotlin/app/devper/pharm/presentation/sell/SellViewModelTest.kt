package app.devper.pharm.presentation.sell

import app.devper.pharm.common.value.Money
import app.devper.pharm.common.value.Quantity

import app.devper.pharm.common.AppDispatchers
import app.devper.pharm.domain.model.CartDiscount
import app.devper.pharm.domain.model.CartLine
import app.devper.pharm.domain.model.CartLineKey
import app.devper.pharm.domain.model.Customer
import app.devper.pharm.domain.model.Drug
import app.devper.pharm.domain.model.Settings
import app.devper.pharm.domain.model.StoreInfo
import app.devper.pharm.domain.observer.CartStateProvider
import app.devper.pharm.domain.observer.SettingsProvider
import app.devper.pharm.domain.repository.FakeCartRepository
import app.devper.pharm.domain.repository.FakeSettingsRepository
import app.devper.pharm.domain.usecase.ClearCartUseCase
import app.devper.pharm.domain.usecase.RefreshSettingsUseCase
import app.devper.pharm.domain.usecase.RemoveCartItemUseCase
import app.devper.pharm.domain.usecase.SetCartDiscountUseCase
import app.devper.pharm.domain.usecase.SetCartQtyUseCase
import app.devper.pharm.domain.usecase.SetCashReceivedUseCase
import app.devper.pharm.domain.usecase.SetLineDiscountUseCase
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
class SellViewModelTest {

    private fun drug(
        id: String = "d1",
        name: String = "Paracetamol",
        stock: Int = 100,
        price: Double = 5.0,
    ) = Drug(
        id = id,
        name = name,
        genericName = null,
        type = null,
        strength = null,
        barcode = null,
        sellPrice = Money(price),
        costPrice = Money(0.0),
        stock = Quantity(stock),
        minStock = Quantity(0),
        unit = "เม็ด",
        regNo = null,
    )

    private fun line(drug: Drug = drug(), qty: Int = 1, discount: Money = Money.Zero) =
        CartLine(drug = drug, qty = qty, discount = discount)

    private fun customer(id: String = "c1", name: String = "John") =
        Customer(id = id, name = name, phone = null, priceTier = "", allergyNote = null)

    private data class Bundle(
        val vm: SellViewModel,
        val cart: FakeCartRepository,
        val settings: FakeSettingsRepository,
    )

    private fun newVm(
        dispatchers: AppDispatchers,
        cart: FakeCartRepository = FakeCartRepository(),
        settings: FakeSettingsRepository = FakeSettingsRepository(),
    ): Bundle {
        val vm = SellViewModel(
            cartState = CartStateProvider(cart),
            setCartQty = SetCartQtyUseCase(cart),
            removeItem = RemoveCartItemUseCase(cart),
            clearCart = ClearCartUseCase(cart),
            setLineDiscount = SetLineDiscountUseCase(cart),
            setCartDiscount = SetCartDiscountUseCase(cart),
            setCashReceived = SetCashReceivedUseCase(cart),
            settings = SettingsProvider(settings),
            refreshSettings = RefreshSettingsUseCase(settings, dispatchers),
        )
        return Bundle(vm, cart, settings)
    }

    @Test
    fun init_subscribes_to_cart_state() = runVmTest { dispatchers ->
        val (vm, cart) = newVm(
            dispatchers,
            FakeCartRepository(
                initialItems = listOf(line(qty = 2)),
                initialCustomer = customer(),
                initialDiscount = CartDiscount.Flat(Money(10.0)),
                initialReceived = "100",
            ),
        )
        advanceUntilIdle()
        val s = vm.state.value
        assertEquals(1, s.cart.size)
        assertEquals(2, s.cart[0].qty)
        assertEquals("John", s.customer?.name)
        assertEquals(CartDiscount.Flat(Money(10.0)), s.cartDiscount)
        assertEquals("100", s.received)

        assertEquals(1, cart.state.value.active.items.size)
    }

    @Test
    fun init_cart_mutations_propagate() = runVmTest { dispatchers ->
        val (vm, cart) = newVm(dispatchers)
        advanceUntilIdle()
        assertEquals(0, vm.state.value.cart.size)
        cart.pushItems(listOf(line(qty = 3)))
        cart.pushCustomer(customer(name = "Jane"))
        advanceUntilIdle()
        assertEquals(3, vm.state.value.cart[0].qty)
        assertEquals("Jane", vm.state.value.customer?.name)
    }

    @Test
    fun init_subscribes_to_settings_state() = runVmTest { dispatchers ->
        val seeded = Settings(store = StoreInfo(name = "Pharm A"))
        val (vm, _, settings) = newVm(dispatchers, settings = FakeSettingsRepository(initialSettings = seeded))
        advanceUntilIdle()
        assertEquals("Pharm A", vm.state.value.settings.store.name)

        settings.pushSettings(Settings(store = StoreInfo(name = "Pharm B")))
        advanceUntilIdle()
        assertEquals("Pharm B", vm.state.value.settings.store.name)
    }

    @Test
    fun init_fires_refreshSettings() = runVmTest { dispatchers ->
        val (_, _, settings) = newVm(dispatchers)
        advanceUntilIdle()
        assertEquals(1, settings.refreshCallCount)
    }

    @Test
    fun onSetQty_delegates_to_setCartQty_usecase() = runVmTest { dispatchers ->
        val (vm, cart) = newVm(dispatchers)
        advanceUntilIdle()
        vm.onSetQty(CartLineKey("d1", null), displayQty = 4)
        advanceUntilIdle()
        assertEquals(CartLineKey("d1", null), cart.lastSetQty?.key)
        assertEquals(4, cart.lastSetQty?.displayQty)
    }

    @Test
    fun onRemove_delegates_to_removeItem_usecase() = runVmTest { dispatchers ->
        val (vm, cart) = newVm(dispatchers, FakeCartRepository(initialItems = listOf(line())))
        advanceUntilIdle()
        vm.onRemove(CartLineKey("d1", null))
        advanceUntilIdle()
        assertEquals(CartLineKey("d1", null), cart.lastRemove)

        assertTrue(vm.state.value.cart.isEmpty())
    }

    @Test
    fun requestClearCart_flips_showClearConfirm_when_cart_non_empty() = runVmTest { dispatchers ->
        val (vm, _) = newVm(dispatchers, FakeCartRepository(initialItems = listOf(line())))
        advanceUntilIdle()
        assertFalse(vm.state.value.showClearConfirm)
        vm.requestClearCart()
        assertTrue(vm.state.value.showClearConfirm)
    }

    @Test
    fun requestClearCart_no_op_when_cart_empty() = runVmTest { dispatchers ->
        val (vm, _) = newVm(dispatchers)
        advanceUntilIdle()
        vm.requestClearCart()
        assertFalse(vm.state.value.showClearConfirm)
    }

    @Test
    fun confirmClearCart_clears_cart_and_resets_flag() = runVmTest { dispatchers ->
        val (vm, cart) = newVm(dispatchers, FakeCartRepository(initialItems = listOf(line())))
        advanceUntilIdle()
        vm.requestClearCart()
        assertTrue(vm.state.value.showClearConfirm)
        vm.confirmClearCart()
        advanceUntilIdle()
        assertTrue(cart.clearCalled)
        assertTrue(vm.state.value.cart.isEmpty())
        assertFalse(vm.state.value.showClearConfirm)
    }

    @Test
    fun cancelClearCart_resets_flag_without_clearing() = runVmTest { dispatchers ->
        val (vm, cart) = newVm(dispatchers, FakeCartRepository(initialItems = listOf(line())))
        advanceUntilIdle()
        vm.requestClearCart()
        assertTrue(vm.state.value.showClearConfirm)
        vm.cancelClearCart()
        advanceUntilIdle()
        assertFalse(cart.clearCalled)
        assertFalse(vm.state.value.showClearConfirm)
        assertTrue(vm.state.value.cart.isNotEmpty())
    }

    @Test
    fun onReceivedChange_delegates_to_setCashReceived_usecase() = runVmTest { dispatchers ->
        val (vm, cart) = newVm(dispatchers)
        vm.onReceivedChange("250.50")
        advanceUntilIdle()
        assertEquals("250.50", cart.lastSetCashReceived)
        assertEquals("250.50", vm.state.value.received)
    }

    @Test
    fun onApplyCartDiscount_delegates_and_closes_sheet() = runVmTest { dispatchers ->
        val (vm, cart) = newVm(dispatchers)
        vm.onOpenCartDiscount()
        assertTrue(vm.state.value.cartDiscountSheetOpen)
        vm.onApplyCartDiscount(CartDiscount.Percent(15.0))
        advanceUntilIdle()
        assertEquals(CartDiscount.Percent(15.0), cart.lastSetCartDiscount)

        assertEquals(false, vm.state.value.cartDiscountSheetOpen)

        assertEquals(CartDiscount.Percent(15.0), vm.state.value.cartDiscount)
    }

    @Test
    fun onApplyLineDiscount_delegates_and_closes_sheet() = runVmTest { dispatchers ->
        val theLine = line()
        val (vm, cart) = newVm(dispatchers, FakeCartRepository(initialItems = listOf(theLine)))
        advanceUntilIdle()
        vm.onOpenLineDiscount(theLine)
        assertNotNull(vm.state.value.lineDiscountFor)
        vm.onApplyLineDiscount(theLine.key, discount = 1.50)
        advanceUntilIdle()
        assertEquals(theLine.key, cart.lastSetLineDiscount?.key)
        assertEquals(1.50, cart.lastSetLineDiscount?.discount)
        assertNull(vm.state.value.lineDiscountFor)
    }

    @Test
    fun line_discount_sheet_open_close_is_local_state() = runVmTest { dispatchers ->
        val theLine = line()
        val (vm, _) = newVm(dispatchers, FakeCartRepository(initialItems = listOf(theLine)))
        advanceUntilIdle()
        vm.onOpenLineDiscount(theLine)
        assertEquals(theLine, vm.state.value.lineDiscountFor)
        vm.onCloseLineDiscount()
        assertNull(vm.state.value.lineDiscountFor)
    }

    @Test
    fun cart_discount_sheet_open_close_is_local_state() = runVmTest { dispatchers ->
        val (vm, _) = newVm(dispatchers)
        advanceUntilIdle()
        vm.onOpenCartDiscount()
        assertTrue(vm.state.value.cartDiscountSheetOpen)
        vm.onCloseCartDiscount()
        assertEquals(false, vm.state.value.cartDiscountSheetOpen)
    }

    @Test
    fun cart_subscription_keeps_lineDiscountFor_when_line_persists() = runVmTest { dispatchers ->
        val initial = line(qty = 1)
        val (vm, cart) = newVm(dispatchers, FakeCartRepository(initialItems = listOf(initial)))
        advanceUntilIdle()
        vm.onOpenLineDiscount(initial)
        assertNotNull(vm.state.value.lineDiscountFor)

        val updated = line(qty = 3)
        cart.pushItems(listOf(updated))
        advanceUntilIdle()
        assertEquals(3, vm.state.value.lineDiscountFor?.qty)
    }

    @Test
    fun cart_subscription_drops_lineDiscountFor_when_line_removed() = runVmTest { dispatchers ->
        val initial = line(qty = 1)
        val (vm, cart) = newVm(dispatchers, FakeCartRepository(initialItems = listOf(initial)))
        advanceUntilIdle()
        vm.onOpenLineDiscount(initial)
        assertNotNull(vm.state.value.lineDiscountFor)

        cart.pushItems(emptyList())
        advanceUntilIdle()
        assertNull(vm.state.value.lineDiscountFor)
    }

    @Test
    fun dismissError_clears_error() = runVmTest { dispatchers ->
        val (vm, _) = newVm(dispatchers)
        advanceUntilIdle()

        assertNull(vm.state.value.error)
        vm.dismissError()
        assertNull(vm.state.value.error)
    }
}
