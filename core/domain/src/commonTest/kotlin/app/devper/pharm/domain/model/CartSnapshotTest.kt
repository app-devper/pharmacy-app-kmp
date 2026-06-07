package app.devper.pharm.domain.model

import app.devper.pharm.common.value.Money
import app.devper.pharm.common.value.Quantity

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class CartSnapshotTest {

    private fun drug(price: Money) = Drug(
        id = "d", name = "D", genericName = null, type = null, strength = null,
        barcode = null, sellPrice = price, costPrice = Money(0.0), stock = Quantity(100), minStock = Quantity(0),
        unit = "เม็ด", regNo = null,
    )

    private fun snapshot(items: List<CartLine>, discount: CartDiscount = CartDiscount.None) =
        CartSnapshot(
            items = items, selectedCustomer = null, cartDiscount = discount,
            activeTier = "", cashReceived = "", lastReceipt = null,
        )

    @Test
    fun total_subtracts_cart_discount_from_subtotal() {
        val snap = snapshot(
            items = listOf(CartLine(drug = drug(Money(10.0)), qty = 3)),
            discount = CartDiscount.Percent(percent = 10.0),
        )
        assertEquals(Money(30.0), snap.subtotal)
        assertEquals(Money(3.0), snap.cartDiscountAmount)
        assertEquals(Money(27.0), snap.total)
        assertFalse(snap.isEmpty)
    }

    @Test
    fun empty_snapshot_is_empty_with_zero_total() {
        val snap = snapshot(items = emptyList())
        assertTrue(snap.isEmpty)
        assertEquals(Money.Zero, snap.subtotal)
        assertEquals(Money.Zero, snap.total)
    }
}
