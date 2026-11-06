package app.devper.pharm.domain.model

import app.devper.pharm.common.value.Money
import app.devper.pharm.common.value.Quantity

import kotlin.test.Test
import kotlin.test.assertEquals

class ParkedCartTest {

    private fun drug(price: Money) = Drug(
        id = "d", name = "D", genericName = null, type = null, strength = null,
        barcode = null, sellPrice = price, costPrice = Money(0.0), stock = Quantity(100), minStock = Quantity(0),
        unit = "เม็ด", regNo = null,
    )

    private fun parked(items: List<CartLine>, discount: CartDiscount = CartDiscount.None) =
        ParkedCart(items = items, cartDiscount = discount, activeTier = "", parkedAt = 0L)

    @Test
    fun parkedCart_total_equals_the_bill_total() {
        val cart = parked(
            items = listOf(CartLine(drug = drug(Money(123.45)), qty = 1)),
            discount = CartDiscount.Percent(percent = 7.0),
        )
        assertEquals(Money(114.81), cart.total)
        assertEquals(cart.bill.total, cart.total)
    }

    @Test
    fun parkedCart_bill_sums_rounded_line_totals() {
        val items = List(3) { CartLine(drug = drug(Money(1.115)), qty = 1) }
        val cart = parked(items)
        assertEquals(Money(3.36), cart.bill.subtotal)
        assertEquals(cart.bill.subtotal.roundToSatang(), cart.bill.subtotal)
    }
}
