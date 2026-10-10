package app.devper.pharm.domain.cart

import app.devper.pharm.common.value.Money
import app.devper.pharm.common.value.Quantity
import app.devper.pharm.domain.extension.Tier
import app.devper.pharm.domain.model.AltUnit
import app.devper.pharm.domain.model.CartDiscount
import app.devper.pharm.domain.model.CartLineKey
import app.devper.pharm.domain.model.Customer
import app.devper.pharm.domain.model.Drug
import app.devper.pharm.domain.model.Sale
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class CartTest {

    private val strip = AltUnit(name = "strip", factor = 10, sellPrice = Money(90.0), prices = mapOf(Tier.Wholesale to Money(80.0)))

    private fun drug(id: String = "d1") = Drug(
        id = id, name = "Paracetamol", genericName = null, type = null, strength = null, barcode = null,
        sellPrice = Money(10.0), costPrice = Money(4.0), stock = Quantity(100), minStock = Quantity.Zero,
        unit = "tab", regNo = null, prices = mapOf(Tier.Wholesale to Money(8.0)), altUnits = listOf(strip),
    )

    private fun customer(tier: String) = Customer(id = "c1", name = "Somchai", phone = null, priceTier = tier, allergyNote = null)

    private val store = InMemoryCartStore()
    private val cart = Cart(store) { 1_000L }

    @Test
    fun adding_an_alternative_unit_counts_base_units_and_adding_again_adds_up() {
        cart.add(drug(), strip)
        cart.add(drug(), strip)
        cart.add(drug())

        val lines = cart.current.items
        assertEquals(listOf(20, 1), lines.map { it.qty })
        assertEquals(2, lines.first().displayQty)
    }

    @Test
    fun choosing_a_customer_reprices_every_line_at_their_tier_and_clearing_goes_back_to_retail() {
        cart.add(drug())
        cart.add(drug(), strip)

        cart.selectCustomer(customer(Tier.Wholesale))
        assertEquals(Money(8.0) + Money(80.0), cart.current.items.fold(Money.Zero) { acc, l -> acc + l.unitPrice })
        assertEquals(Tier.Wholesale, cart.current.activeTier)

        cart.clearCustomer()
        assertTrue(cart.current.items.all { it.tier == Tier.Retail })
        assertNull(cart.current.selectedCustomer)
    }

    @Test
    fun a_customer_without_a_tier_buys_at_retail() {
        cart.add(drug())
        cart.selectCustomer(customer(""))
        assertEquals(Tier.Retail, cart.current.activeTier)
    }

    @Test
    fun a_line_discount_is_capped_at_the_base_price_and_never_negative() {
        cart.add(drug())
        val key = CartLineKey("d1", null)

        cart.setLineDiscount(key, 25.0)
        assertEquals(Money(10.0), cart.current.items.single().discount)
        cart.setLineDiscount(key, -3.0)
        assertEquals(Money.Zero, cart.current.items.single().discount)
    }

    @Test
    fun setting_a_quantity_is_in_the_unit_shown_and_zero_removes_the_line() {
        cart.add(drug(), strip)
        val key = CartLineKey("d1", "strip")

        cart.setQty(key, 3)
        assertEquals(30, cart.current.items.single().qty)
        cart.setQty(key, 0)
        assertTrue(cart.current.isEmpty)
    }

    @Test
    fun the_active_cart_is_kept_in_the_store_and_restored_by_a_new_cart() {
        cart.add(drug())
        cart.setCashReceived("100")
        cart.setCartDiscount(CartDiscount.Percent(10.0))

        val reopened = Cart(store)
        assertEquals(cart.current.items, reopened.current.items)
        assertEquals("100", reopened.current.cashReceived)

        cart.clear()
        assertNull(store.active)
    }

    @Test
    fun parking_moves_the_cart_to_a_slot_and_restoring_brings_it_back() {
        cart.add(drug())
        cart.selectCustomer(customer(Tier.Wholesale))

        cart.park(2)
        assertTrue(cart.current.isEmpty)
        assertEquals(1_000L, cart.parkedSlots.value[2]?.parkedAt)
        assertEquals(cart.parkedSlots.value[2], store.parked[2])

        cart.restore(2)
        assertEquals(Tier.Wholesale, cart.current.activeTier)
        assertNull(cart.parkedSlots.value[2])
        assertNull(store.parked[2])
    }

    @Test
    fun an_empty_cart_or_a_slot_out_of_range_parks_nothing() {
        cart.park(0)
        cart.add(drug())
        cart.park(PARK_SLOT_COUNT)
        cart.restore(-1)

        assertTrue(cart.parkedSlots.value.all { it == null })
        assertEquals(1, cart.current.items.size)
    }

    @Test
    fun a_receipt_empties_the_cart_and_dismissing_it_keeps_the_cart_empty() {
        cart.add(drug())
        val sale = Sale(id = "s1", billNo = "INV-1", total = Money(10.0), change = Money.Zero, discount = Money.Zero, stockUpdates = emptyList())

        cart.commitReceipt(sale)
        assertEquals(sale, cart.current.lastReceipt)
        assertTrue(cart.current.isEmpty)

        cart.dismissReceipt()
        assertNull(cart.current.lastReceipt)
    }
}
