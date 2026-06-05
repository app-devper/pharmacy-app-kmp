package app.devper.pharm.domain.model

import app.devper.pharm.domain.pricing.Tier

data class CartSnapshot(
    val items: List<CartLine>,
    val selectedCustomer: Customer?,
    val cartDiscount: CartDiscount,

    val activeTier: String,
    val cashReceived: String,
    val lastReceipt: Sale?,
) {
    val isEmpty: Boolean get() = items.isEmpty()
    val subtotal: Double get() = items.sumOf { it.lineTotal }
    val cartDiscountAmount: Double get() = cartDiscount.apply(subtotal)
    val total: Double get() = (subtotal - cartDiscountAmount).coerceAtLeast(0.0)
}
