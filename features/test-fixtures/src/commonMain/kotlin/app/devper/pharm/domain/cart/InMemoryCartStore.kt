package app.devper.pharm.domain.cart

import app.devper.pharm.domain.extension.Tier
import app.devper.pharm.domain.model.ActiveCart
import app.devper.pharm.domain.model.CartDiscount
import app.devper.pharm.domain.model.CartLine
import app.devper.pharm.domain.model.Customer
import app.devper.pharm.domain.model.ParkedCart
import app.devper.pharm.domain.model.Sale

class InMemoryCartStore(
    var active: ActiveCart? = null,
    parked: List<ParkedCart?> = List(PARK_SLOT_COUNT) { null },
) : CartStore {
    val parked: MutableList<ParkedCart?> = parked.toMutableList()

    override fun loadActive(): ActiveCart? = active
    override fun saveActive(active: ActiveCart) { this.active = active }
    override fun clearActive() { active = null }
    override fun loadParked(): List<ParkedCart?> = parked.toList()
    override fun saveParked(slot: Int, parked: ParkedCart) { this.parked[slot] = parked }
    override fun clearParked(slot: Int) { parked[slot] = null }
}

fun testCart(
    items: List<CartLine> = emptyList(),
    customer: Customer? = null,
    discount: CartDiscount = CartDiscount.None,
    tier: String = Tier.Retail,
    received: String = "",
    receipt: Sale? = null,
    parked: List<ParkedCart?> = List(PARK_SLOT_COUNT) { null },
    now: () -> Long = { 0L },
): Cart {
    val active = ActiveCart(items = items, customer = customer, cartDiscount = discount, activeTier = tier, cashReceived = received)
    val cart = Cart(InMemoryCartStore(active.takeIf { it != ActiveCart.Empty }, parked), now)
    receipt?.let { cart.commitReceipt(it) }
    return cart
}
