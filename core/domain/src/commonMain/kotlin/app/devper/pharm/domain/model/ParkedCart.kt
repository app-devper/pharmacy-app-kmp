package app.devper.pharm.domain.model

import app.devper.pharm.common.value.Money

data class ParkedCart(
    val items: List<CartLine>,
    val customer: Customer? = null,
    val cartDiscount: CartDiscount = CartDiscount.None,
    val activeTier: String,
    val cashReceived: String = "",
    val parkedAt: Long,
) {
    val itemCount: Int get() = items.sumOf { it.qty }
    val total: Money
        get() {
            val subtotal = items.fold(Money.Zero) { acc, line -> acc + line.lineTotal }
            return (subtotal - cartDiscount.apply(subtotal)).coerceAtLeast(Money.Zero)
        }
}
