package app.devper.pharm.domain.model

import app.devper.pharm.domain.pricing.Tier

data class ActiveCart(
    val items: List<CartLine> = emptyList(),
    val customer: Customer? = null,
    val cartDiscount: CartDiscount = CartDiscount.None,
    val activeTier: String = Tier.Retail,
    val cashReceived: String = "",
) {
    companion object {
        val Empty: ActiveCart = ActiveCart()
    }
}

data class CartState(
    val active: ActiveCart = ActiveCart.Empty,
    val lastReceipt: Sale? = null,
) {
    companion object {
        val Empty: CartState = CartState()
    }
}
