package app.devper.pharm.domain.model

import app.devper.pharm.common.value.Money

data class PricedBill(
    val grossSubtotal: Money,
    val subtotal: Money,
    val itemDiscountTotal: Money,
    val cartDiscountAmount: Money,
    val total: Money,
) {

    fun change(received: Money): Money = (received - total).coerceAtLeast(Money.Zero).roundToSatang()

    fun shortfall(received: Money): Money = (total - received).coerceAtLeast(Money.Zero).roundToSatang()

    companion object {
        val Empty: PricedBill = PricedBill(
            grossSubtotal = Money.Zero,
            subtotal = Money.Zero,
            itemDiscountTotal = Money.Zero,
            cartDiscountAmount = Money.Zero,
            total = Money.Zero,
        )
    }
}

fun priceBill(items: List<CartLine>, cartDiscount: CartDiscount): PricedBill {
    val grossSubtotal = items.fold(Money.Zero) { acc, line -> acc + line.grossTotal }.roundToSatang()
    val subtotal = items.fold(Money.Zero) { acc, line -> acc + line.lineTotal.roundToSatang() }.roundToSatang()
    val cartDiscountAmount = cartDiscount.apply(subtotal).roundToSatang()
    return PricedBill(
        grossSubtotal = grossSubtotal,
        subtotal = subtotal,
        itemDiscountTotal = (grossSubtotal - subtotal).roundToSatang(),
        cartDiscountAmount = cartDiscountAmount,
        total = (subtotal - cartDiscountAmount).coerceAtLeast(Money.Zero).roundToSatang(),
    )
}
