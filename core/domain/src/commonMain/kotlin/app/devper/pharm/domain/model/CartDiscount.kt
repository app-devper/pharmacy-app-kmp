package app.devper.pharm.domain.model

sealed class CartDiscount {
    object None : CartDiscount()
    data class Flat(val amount: Double) : CartDiscount()
    data class Percent(val percent: Double) : CartDiscount()

    fun apply(subtotal: Double): Double = when (this) {
        is None     -> 0.0
        is Flat     -> amount.coerceIn(0.0, subtotal)
        is Percent  -> (subtotal * (percent.coerceIn(0.0, 100.0) / 100.0)).coerceAtMost(subtotal)
    }
}
