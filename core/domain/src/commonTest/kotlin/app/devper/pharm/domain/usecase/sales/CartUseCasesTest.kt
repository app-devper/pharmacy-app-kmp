package app.devper.pharm.domain.usecase

import app.devper.pharm.domain.usecase.sales.AddToCartUseCase
import app.devper.pharm.domain.usecase.sales.ClearCartUseCase
import app.devper.pharm.domain.usecase.sales.ClearCustomerUseCase
import app.devper.pharm.domain.usecase.sales.DiscardParkedCartUseCase
import app.devper.pharm.domain.usecase.sales.DismissReceiptUseCase
import app.devper.pharm.domain.usecase.sales.ParkCartUseCase
import app.devper.pharm.domain.usecase.sales.RemoveCartItemUseCase
import app.devper.pharm.domain.usecase.sales.RestoreCartUseCase
import app.devper.pharm.domain.usecase.sales.SelectCustomerUseCase
import app.devper.pharm.domain.usecase.sales.SetCartDiscountUseCase
import app.devper.pharm.domain.usecase.sales.SetCartQtyUseCase
import app.devper.pharm.domain.usecase.sales.SetCashReceivedUseCase
import app.devper.pharm.domain.usecase.sales.SetLineDiscountUseCase

import app.devper.pharm.common.value.Money
import app.devper.pharm.common.value.Quantity
import app.devper.pharm.domain.model.AltUnit
import app.devper.pharm.domain.model.CartDiscount
import app.devper.pharm.domain.model.CartLine
import app.devper.pharm.domain.model.CartLineKey
import app.devper.pharm.domain.model.Customer
import app.devper.pharm.domain.model.Drug
import app.devper.pharm.domain.model.Sale
import app.devper.pharm.domain.param.sales.AddCartItemParam
import app.devper.pharm.domain.param.sales.SetCartQtyParam
import app.devper.pharm.domain.param.sales.SetLineDiscountParam
import app.devper.pharm.domain.repository.FakeCartRepository
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

private fun drug(id: String = "d1", name: String = "Drug $id") = Drug(
    id = id, name = name, genericName = null, type = null, strength = null,
    barcode = null, sellPrice = Money(10.0), costPrice = Money(0.0),
    stock = Quantity(100), minStock = Quantity.Zero,
    unit = "เม็ด", regNo = null,
)

private fun line(drug: Drug = drug(), qty: Int = 1, selectedUnit: AltUnit? = null) =
    CartLine(drug = drug, qty = qty, selectedUnit = selectedUnit)

private fun customer(id: String = "c1", name: String = "John") =
    Customer(id = id, name = name, phone = null, priceTier = "", allergyNote = null)

private fun sale(id: String = "s1", billNo: String = "B1") = Sale(
    id = id, billNo = billNo,
    total = Money(100.0), change = Money.Zero, discount = Money.Zero,
    stockUpdates = emptyList(),
)

class AddToCartUseCaseTest {

    @Test
    fun dispatches_param_to_repository() {
        val repo = FakeCartRepository()
        val param = AddCartItemParam(drug = drug(), altUnit = null)

        val result = AddToCartUseCase(repo).invoke(param)

        assertTrue(result.isSuccess)
        assertEquals(param, repo.lastAdd)
    }

    @Test
    fun convenience_invoke_builds_param_with_alt_unit() {
        val repo = FakeCartRepository()
        val box = AltUnit(name = "กล่อง", factor = 10, sellPrice = Money(100.0))

        AddToCartUseCase(repo).invoke(drug(), altUnit = box)

        assertEquals(drug(), repo.lastAdd?.drug)
        assertEquals(box, repo.lastAdd?.altUnit)
    }

    @Test
    fun convenience_invoke_without_alt_unit_passes_null() {
        val repo = FakeCartRepository()

        AddToCartUseCase(repo).invoke(drug())

        assertNull(repo.lastAdd?.altUnit)
    }
}

class SetCartQtyUseCaseTest {

    @Test
    fun dispatches_param_to_repository() {
        val repo = FakeCartRepository()
        val key = CartLineKey("d1", null)
        val param = SetCartQtyParam(key = key, displayQty = 5)

        SetCartQtyUseCase(repo).invoke(param)

        assertEquals(param, repo.lastSetQty)
    }

    @Test
    fun convenience_invoke_builds_param_from_key_and_qty() {
        val repo = FakeCartRepository()
        val key = CartLineKey("d1", "กล่อง")

        SetCartQtyUseCase(repo).invoke(key, displayQty = 3)

        assertEquals(key, repo.lastSetQty?.key)
        assertEquals(3, repo.lastSetQty?.displayQty)
    }
}

class SetLineDiscountUseCaseTest {

    @Test
    fun dispatches_param_to_repository() {
        val repo = FakeCartRepository()
        val key = CartLineKey("d1", null)
        val param = SetLineDiscountParam(key = key, discount = 2.5)

        SetLineDiscountUseCase(repo).invoke(param)

        assertEquals(param, repo.lastSetLineDiscount)
    }

    @Test
    fun convenience_invoke_builds_param_from_key_and_discount() {
        val repo = FakeCartRepository()
        val key = CartLineKey("d1", null)

        SetLineDiscountUseCase(repo).invoke(key, discount = 1.5)

        assertEquals(key, repo.lastSetLineDiscount?.key)
        assertEquals(1.5, repo.lastSetLineDiscount?.discount)
    }
}

class SetCartDiscountUseCaseTest {

    @Test
    fun dispatches_flat_discount_to_repository() {
        val repo = FakeCartRepository()

        SetCartDiscountUseCase(repo).invoke(CartDiscount.Flat(Money(10.0)))

        assertEquals(CartDiscount.Flat(Money(10.0)), repo.lastSetCartDiscount)
    }

    @Test
    fun dispatches_percent_discount_to_repository() {
        val repo = FakeCartRepository()

        SetCartDiscountUseCase(repo).invoke(CartDiscount.Percent(15.0))

        assertEquals(CartDiscount.Percent(15.0), repo.lastSetCartDiscount)
    }

    @Test
    fun dispatches_none_to_repository() {
        val repo = FakeCartRepository(initialDiscount = CartDiscount.Flat(Money(5.0)))

        SetCartDiscountUseCase(repo).invoke(CartDiscount.None)

        assertEquals(CartDiscount.None, repo.lastSetCartDiscount)
        assertEquals(CartDiscount.None, repo.state.value.active.cartDiscount)
    }
}

class ClearCartUseCaseTest {

    @Test
    fun invoke_clears_active_cart() {
        val repo = FakeCartRepository(
            initialItems = listOf(line(qty = 2)),
            initialCustomer = customer(),
            initialDiscount = CartDiscount.Flat(Money(10.0)),
            initialReceived = "100",
        )

        ClearCartUseCase(repo).invoke()

        assertTrue(repo.clearCalled)
        assertTrue(repo.state.value.active.items.isEmpty())
        assertNull(repo.state.value.active.customer)
        assertEquals(CartDiscount.None, repo.state.value.active.cartDiscount)
        assertEquals("", repo.state.value.active.cashReceived)
    }
}

class RemoveCartItemUseCaseTest {

    @Test
    fun removes_line_by_key() {
        val repo = FakeCartRepository(initialItems = listOf(line(drug = drug("a")), line(drug = drug("b"))))
        val key = CartLineKey("a", null)

        RemoveCartItemUseCase(repo).invoke(key)

        assertEquals(key, repo.lastRemove)
        assertEquals(1, repo.state.value.active.items.size)
        assertEquals("b", repo.state.value.active.items[0].drug.id)
    }
}

class SetCashReceivedUseCaseTest {

    @Test
    fun dispatches_string_value_to_repository() {
        val repo = FakeCartRepository()

        SetCashReceivedUseCase(repo).invoke("250")

        assertEquals("250", repo.lastSetCashReceived)
        assertEquals("250", repo.state.value.active.cashReceived)
    }

    @Test
    fun empty_string_clears_received() {
        val repo = FakeCartRepository(initialReceived = "100")

        SetCashReceivedUseCase(repo).invoke("")

        assertEquals("", repo.state.value.active.cashReceived)
    }
}

class SelectCustomerUseCaseTest {

    @Test
    fun stores_customer_on_active_cart() {
        val repo = FakeCartRepository()
        val c = customer()

        SelectCustomerUseCase(repo).invoke(c)

        assertEquals(c, repo.lastSelectCustomer)
        assertEquals(c, repo.state.value.active.customer)
    }
}

class ClearCustomerUseCaseTest {

    @Test
    fun invoke_clears_active_customer() {
        val repo = FakeCartRepository(initialCustomer = customer())

        ClearCustomerUseCase(repo).invoke()

        assertTrue(repo.clearCustomerCalled)
        assertNull(repo.state.value.active.customer)
    }
}

class ParkCartUseCaseTest {

    @Test
    fun parks_cart_to_given_slot() {
        val repo = FakeCartRepository(initialItems = listOf(line(qty = 2)))

        ParkCartUseCase(repo).invoke(2)

        assertEquals(2, repo.lastParkSlot)
        assertNotNull(repo.parkedSlots.value[2])
        assertTrue(repo.state.value.active.items.isEmpty())
    }

    @Test
    fun parking_empty_cart_is_noop() {
        val repo = FakeCartRepository()

        val result = ParkCartUseCase(repo).invoke(0)

        assertTrue(result.isSuccess)
        assertNull(repo.parkedSlots.value[0])
    }
}

class RestoreCartUseCaseTest {

    @Test
    fun restores_parked_cart_into_active() {
        val repo = FakeCartRepository(initialItems = listOf(line(qty = 3)))
        ParkCartUseCase(repo).invoke(1)
        assertTrue(repo.state.value.active.items.isEmpty())

        RestoreCartUseCase(repo).invoke(1)

        assertEquals(1, repo.lastRestoreSlot)
        assertEquals(1, repo.state.value.active.items.size)
        assertNull(repo.parkedSlots.value[1])
    }

    @Test
    fun restoring_empty_slot_is_noop() {
        val repo = FakeCartRepository()

        val result = RestoreCartUseCase(repo).invoke(0)

        assertTrue(result.isSuccess)
        assertTrue(repo.state.value.active.items.isEmpty())
    }
}

class DiscardParkedCartUseCaseTest {

    @Test
    fun discards_slot_without_restoring() {
        val repo = FakeCartRepository(initialItems = listOf(line()))
        ParkCartUseCase(repo).invoke(3)

        DiscardParkedCartUseCase(repo).invoke(3)

        assertEquals(3, repo.lastDiscardSlot)
        assertNull(repo.parkedSlots.value[3])
        assertTrue(repo.state.value.active.items.isEmpty())
    }
}

class DismissReceiptUseCaseTest {

    @Test
    fun invoke_clears_last_receipt() {
        val repo = FakeCartRepository(initialReceipt = sale())
        assertNotNull(repo.state.value.lastReceipt)

        DismissReceiptUseCase(repo).invoke()

        assertTrue(repo.dismissReceiptCalled)
        assertNull(repo.state.value.lastReceipt)
    }

    @Test
    fun dismiss_with_no_receipt_is_noop() {
        val repo = FakeCartRepository()
        assertNull(repo.state.value.lastReceipt)

        val result = DismissReceiptUseCase(repo).invoke()

        assertTrue(result.isSuccess)
        assertFalse(repo.state.value.lastReceipt != null)
    }
}
