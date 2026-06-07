package app.devper.pharm.domain.model

import app.devper.pharm.common.value.Money

sealed class CartDiscount {
    object None : CartDiscount()
    data class Flat(val amount: Money) : CartDiscount()
    data class Percent(val percent: Double) : CartDiscount()

    fun apply(subtotal: Money): Money = when (this) {
        is None    -> Money.Zero
        is Flat    -> amount.coerceAtLeast(Money.Zero).coerceAtMost(subtotal)
        is Percent -> (subtotal * (percent.coerceIn(0.0, 100.0) / 100.0)).coerceAtMost(subtotal)
    }
}
