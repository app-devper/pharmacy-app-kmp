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
    val bill: PricedBill get() = priceBill(items, cartDiscount)
    val subtotal: Money get() = bill.subtotal
    val cartDiscountAmount: Money get() = bill.cartDiscountAmount
    val total: Money get() = bill.total
}
