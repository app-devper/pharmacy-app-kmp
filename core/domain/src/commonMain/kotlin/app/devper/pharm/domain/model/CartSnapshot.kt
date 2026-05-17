package app.devper.pharm.domain.model

import app.devper.pharm.domain.pricing.Tier

data class CartSnapshot(
    val items: List<CartLine>,
    val selectedCustomer: Customer?,
    val cartDiscount: CartDiscount,

    val activeTier: String,
    val cashReceived: String,
    val lastReceipt: Sale?,
)
