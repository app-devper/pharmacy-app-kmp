package app.devper.pharm.common.value

import kotlin.jvm.JvmInline
import kotlin.math.abs
import kotlin.math.roundToLong

@JvmInline
value class Money(val amount: Double) : Comparable<Money> {

    operator fun plus(other: Money): Money = Money(amount + other.amount)
    operator fun minus(other: Money): Money = Money(amount - other.amount)
    operator fun times(multiplier: Int): Money = Money(amount * multiplier)
    operator fun times(multiplier: Double): Money = Money(amount * multiplier)
    operator fun div(divisor: Int): Money = Money(amount / divisor)
    operator fun unaryMinus(): Money = Money(-amount)

    fun coerceAtLeast(minimum: Money): Money = if (amount < minimum.amount) minimum else this
    fun coerceAtMost(maximum: Money): Money = if (amount > maximum.amount) maximum else this

    override operator fun compareTo(other: Money): Int = amount.compareTo(other.amount)

    val isZero: Boolean get() = amount == 0.0
    val isPositive: Boolean get() = amount > 0.0
    val isNegative: Boolean get() = amount < 0.0

    fun absoluteValue(): Money = Money(abs(amount))

    fun format(decimals: Int = 2): String {
        require(decimals in 0..6) { "decimals must be 0..6" }
        val factor = pow10(decimals)
        val rounded = (amount * factor).roundToLong()
        val whole = rounded / factor
        if (decimals == 0) return whole.toString()
        val frac = abs(rounded % factor).toString().padStart(decimals, '0')
        val sign = if (amount < 0 && whole == 0L) "-" else ""
        return "$sign$whole.$frac"
    }

    override fun toString(): String = format()

    companion object {
        val Zero: Money = Money(0.0)
        fun parse(value: String): Money? = value.trim().toDoubleOrNull()?.let(::Money)
    }
}

private fun pow10(n: Int): Long {
    var r = 1L
    repeat(n) { r *= 10 }
    return r
}
