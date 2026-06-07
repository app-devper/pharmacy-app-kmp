package app.devper.pharm.domain.model

import app.devper.pharm.common.value.Money
import app.devper.pharm.domain.extension.Tier

data class CartSnapshot(
    val items: List<CartLine>,
    val selectedCustomer: Customer?,
    val cartDiscount: CartDiscount,

    val activeTier: String,
    val cashReceived: String,
    val lastReceipt: Sale?,
) {
    val isEmpty: Boolean get() = items.isEmpty()
    val subtotal: Money get() = items.fold(Money.Zero) { acc, line -> acc + line.lineTotal }
    val cartDiscountAmount: Money get() = cartDiscount.apply(subtotal)
    val total: Money get() = (subtotal - cartDiscountAmount).coerceAtLeast(Money.Zero)
}
