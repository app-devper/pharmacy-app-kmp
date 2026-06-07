package app.devper.pharm.domain.model

data class ParkedCart(
    val items: List<CartLine>,
    val customer: Customer? = null,
    val cartDiscount: CartDiscount = CartDiscount.None,
    val activeTier: String,
    val cashReceived: String = "",
    val parkedAt: Long,
) {
    val itemCount: Int get() = items.sumOf { it.qty }
    val total: Double
        get() {
            val subtotal = items.sumOf { it.lineTotal.amount }
            return (subtotal - cartDiscount.apply(subtotal)).coerceAtLeast(0.0)
        }
}
