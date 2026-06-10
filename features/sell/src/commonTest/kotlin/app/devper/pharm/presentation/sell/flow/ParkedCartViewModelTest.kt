package app.devper.pharm.presentation.sell.flow

import app.devper.pharm.common.value.Money
import app.devper.pharm.common.value.Quantity

import app.devper.pharm.common.AppDispatchers
import app.devper.pharm.domain.model.CartLine
import app.devper.pharm.domain.model.Drug
import app.devper.pharm.domain.model.ParkedCart
import app.devper.pharm.domain.observer.CartStateProvider
import app.devper.pharm.domain.observer.ParkedCartsProvider
import app.devper.pharm.domain.repository.FakeCartRepository
import app.devper.pharm.domain.repository.sales.PARK_SLOT_COUNT
import app.devper.pharm.domain.usecase.sales.DiscardParkedCartUseCase
import app.devper.pharm.domain.usecase.sales.ParkCartUseCase
import app.devper.pharm.domain.usecase.sales.RestoreCartUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import app.devper.pharm.ui.common.runVmTest

@OptIn(ExperimentalCoroutinesApi::class)
class ParkedCartViewModelTest {

    private fun drug(id: String = "d1", name: String = "Paracetamol") = Drug(
        id = id, name = name, genericName = null, type = null, strength = null,
        barcode = null, sellPrice = Money(5.0), costPrice = Money(0.0), stock = Quantity(100), minStock = Quantity(0),
        unit = "เม็ด", regNo = null,
    )

    private fun line(qty: Int = 1) = CartLine(drug = drug(), qty = qty)

    private fun parked(itemQty: Int = 2): ParkedCart = ParkedCart(
        items = listOf(line(qty = itemQty)),
        customer = null,
        cashReceived = "",
        activeTier = "RETAIL",
        parkedAt = 12345L,
    )

    private data class Bundle(
        val vm: ParkedCartViewModel,
        val cart: FakeCartRepository,
    )

    private fun newVm(
        @Suppress("UNUSED_PARAMETER") dispatchers: AppDispatchers,
        cart: FakeCartRepository = FakeCartRepository(),
    ): Bundle {
        val vm = ParkedCartViewModel(
            parkedCarts = ParkedCartsProvider(cart),
            cartState = CartStateProvider(cart),
            parkCart = ParkCartUseCase(cart),
            restoreCart = RestoreCartUseCase(cart),
            discardParked = DiscardParkedCartUseCase(cart),
        )
        return Bundle(vm, cart)
    }

    @Test
    fun init_subscribes_to_parkedSlots_and_activeCart_emptiness() = runVmTest { dispatchers ->
        val seed = List<ParkedCart?>(PARK_SLOT_COUNT) { i ->
            if (i == 2) parked() else null
        }
        val (vm) = newVm(
            dispatchers,
            FakeCartRepository(initialItems = listOf(line()), initialParkedSlots = seed),
        )
        advanceUntilIdle()
        assertEquals(PARK_SLOT_COUNT, vm.state.value.parkedSlots.size)
        assertNotNull(vm.state.value.parkedSlots[2])
        assertEquals(1, vm.state.value.filledCount)

        assertFalse(vm.state.value.activeCartIsEmpty)
    }

    @Test
    fun parkedSlots_mutations_propagate_to_state() = runVmTest { dispatchers ->
        val (vm, cart) = newVm(dispatchers, FakeCartRepository(initialItems = listOf(line())))
        advanceUntilIdle()
        assertEquals(0, vm.state.value.filledCount)

        cart.parkCart(slot = 0)
        advanceUntilIdle()
        assertEquals(1, vm.state.value.filledCount)
        assertNotNull(vm.state.value.parkedSlots[0])
    }

    @Test
    fun tapSlot_on_empty_slot_with_non_empty_active_cart_parks() = runVmTest { dispatchers ->
        val (vm, cart) = newVm(dispatchers, FakeCartRepository(initialItems = listOf(line(qty = 4))))
        advanceUntilIdle()
        vm.openSheet()
        vm.tapSlot(slot = 1)
        advanceUntilIdle()

        assertEquals(1, cart.lastParkSlot)

        assertNotNull(cart.parkedSlots.value[1])
        assertEquals(4, cart.parkedSlots.value[1]!!.items[0].qty)
        assertTrue(cart.state.value.active.items.isEmpty())

        assertFalse(vm.state.value.sheetOpen)
    }

    @Test
    fun tapSlot_on_empty_slot_with_empty_active_cart_is_no_op() = runVmTest { dispatchers ->
        val (vm, cart) = newVm(dispatchers)
        advanceUntilIdle()
        vm.openSheet()
        vm.tapSlot(slot = 0)
        advanceUntilIdle()

        assertNull(cart.lastParkSlot)

        assertTrue(vm.state.value.sheetOpen)
    }

    @Test
    fun tapSlot_on_filled_slot_restores() = runVmTest { dispatchers ->
        val seed = List<ParkedCart?>(PARK_SLOT_COUNT) { i ->
            if (i == 3) parked(itemQty = 7) else null
        }
        val (vm, cart) = newVm(
            dispatchers,
            FakeCartRepository(initialParkedSlots = seed),
        )
        advanceUntilIdle()
        vm.openSheet()
        vm.tapSlot(slot = 3)
        advanceUntilIdle()
        assertEquals(3, cart.lastRestoreSlot)

        assertNull(cart.parkedSlots.value[3])
        assertEquals(1, cart.state.value.active.items.size)
        assertEquals(7, cart.state.value.active.items[0].qty)
        assertFalse(vm.state.value.sheetOpen)
    }

    @Test
    fun requestOverwrite_no_op_when_active_cart_empty() = runVmTest { dispatchers ->
        val (vm, _) = newVm(dispatchers)
        advanceUntilIdle()
        vm.requestOverwrite(slot = 0)

        assertNull(vm.state.value.overwriteSlot)
    }

    @Test
    fun requestOverwrite_then_cancel_does_not_park() = runVmTest { dispatchers ->
        val (vm, cart) = newVm(dispatchers, FakeCartRepository(initialItems = listOf(line())))
        advanceUntilIdle()
        vm.requestOverwrite(slot = 2)
        assertEquals(2, vm.state.value.overwriteSlot)
        vm.cancelOverwrite()
        assertNull(vm.state.value.overwriteSlot)
        assertNull(cart.lastParkSlot)
    }

    @Test
    fun confirmOverwrite_parks_into_pending_slot_and_clears_state() = runVmTest { dispatchers ->
        val (vm, cart) = newVm(dispatchers, FakeCartRepository(initialItems = listOf(line(qty = 9))))
        advanceUntilIdle()
        vm.openSheet()
        vm.requestOverwrite(slot = 4)
        vm.confirmOverwrite()
        advanceUntilIdle()
        assertEquals(4, cart.lastParkSlot)
        assertNotNull(cart.parkedSlots.value[4])
        assertNull(vm.state.value.overwriteSlot)
        assertFalse(vm.state.value.sheetOpen)
    }

    @Test
    fun confirmOverwrite_no_op_when_pending_slot_null() = runVmTest { dispatchers ->
        val (vm, cart) = newVm(dispatchers, FakeCartRepository(initialItems = listOf(line())))
        advanceUntilIdle()

        vm.confirmOverwrite()
        advanceUntilIdle()
        assertNull(cart.lastParkSlot)
    }

    @Test
    fun tapSlot_with_filled_slot_and_non_empty_active_sets_swapSlot() = runVmTest { dispatchers ->
        val seed = List<ParkedCart?>(PARK_SLOT_COUNT) { i ->
            if (i == 2) parked(itemQty = 6) else null
        }
        val (vm, cart) = newVm(
            dispatchers,
            FakeCartRepository(initialItems = listOf(line(qty = 3)), initialParkedSlots = seed),
        )
        advanceUntilIdle()
        vm.openSheet()
        vm.tapSlot(slot = 2)
        advanceUntilIdle()

        assertEquals(2, vm.state.value.swapSlot)
        assertNull(cart.lastRestoreSlot)
        assertNotNull(cart.parkedSlots.value[2])
        assertEquals(3, cart.state.value.active.items[0].qty)
        assertTrue(vm.state.value.sheetOpen)
    }

    @Test
    fun cancelSwap_clears_swapSlot_without_restoring() = runVmTest { dispatchers ->
        val seed = List<ParkedCart?>(PARK_SLOT_COUNT) { i ->
            if (i == 0) parked(itemQty = 4) else null
        }
        val (vm, cart) = newVm(
            dispatchers,
            FakeCartRepository(initialItems = listOf(line(qty = 2)), initialParkedSlots = seed),
        )
        advanceUntilIdle()
        vm.tapSlot(slot = 0)
        assertEquals(0, vm.state.value.swapSlot)

        vm.cancelSwap()
        advanceUntilIdle()
        assertNull(vm.state.value.swapSlot)
        assertNull(cart.lastRestoreSlot)
        assertNotNull(cart.parkedSlots.value[0])
    }

    @Test
    fun confirmSwap_restores_and_clears_state() = runVmTest { dispatchers ->
        val seed = List<ParkedCart?>(PARK_SLOT_COUNT) { i ->
            if (i == 1) parked(itemQty = 5) else null
        }
        val (vm, cart) = newVm(
            dispatchers,
            FakeCartRepository(initialItems = listOf(line(qty = 2)), initialParkedSlots = seed),
        )
        advanceUntilIdle()
        vm.openSheet()
        vm.tapSlot(slot = 1)
        assertEquals(1, vm.state.value.swapSlot)

        vm.confirmSwap()
        advanceUntilIdle()
        assertEquals(1, cart.lastRestoreSlot)
        assertEquals(5, cart.state.value.active.items[0].qty)
        assertNull(cart.parkedSlots.value[1])
        assertNull(vm.state.value.swapSlot)
        assertFalse(vm.state.value.sheetOpen)
    }

    @Test
    fun confirmSwap_no_op_when_swapSlot_null() = runVmTest { dispatchers ->
        val (vm, cart) = newVm(dispatchers, FakeCartRepository(initialItems = listOf(line())))
        advanceUntilIdle()
        vm.confirmSwap()
        advanceUntilIdle()
        assertNull(cart.lastRestoreSlot)
    }

    @Test
    fun discard_clears_the_slot() = runVmTest { dispatchers ->
        val seed = List<ParkedCart?>(PARK_SLOT_COUNT) { i ->
            if (i == 1) parked() else null
        }
        val (vm, cart) = newVm(dispatchers, FakeCartRepository(initialParkedSlots = seed))
        advanceUntilIdle()
        vm.discard(slot = 1)
        advanceUntilIdle()
        assertEquals(1, cart.lastDiscardSlot)
        assertNull(cart.parkedSlots.value[1])
        assertEquals(0, vm.state.value.filledCount)
    }
}
