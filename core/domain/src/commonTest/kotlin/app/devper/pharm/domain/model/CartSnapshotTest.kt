package app.devper.pharm.domain.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class CartSnapshotTest {

    private fun drug(price: Double) = Drug(
        id = "d", name = "D", genericName = null, type = null, strength = null,
        barcode = null, sellPrice = price, costPrice = 0.0, stock = 100, minStock = 0,
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
            items = listOf(CartLine(drug = drug(10.0), qty = 3)),
            discount = CartDiscount.Percent(percent = 10.0),
        )
        assertEquals(30.0, snap.subtotal)
        assertEquals(3.0, snap.cartDiscountAmount)
        assertEquals(27.0, snap.total)
        assertFalse(snap.isEmpty)
    }

    @Test
    fun empty_snapshot_is_empty_with_zero_total() {
        val snap = snapshot(items = emptyList())
        assertTrue(snap.isEmpty)
        assertEquals(0.0, snap.subtotal)
        assertEquals(0.0, snap.total)
    }
}
