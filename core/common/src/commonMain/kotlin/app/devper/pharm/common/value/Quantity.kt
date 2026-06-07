package app.devper.pharm.common.value

import kotlin.jvm.JvmInline
import kotlin.math.abs

@JvmInline
value class Quantity(val value: Int) : Comparable<Quantity> {

    operator fun plus(other: Quantity): Quantity = Quantity(value + other.value)
    operator fun minus(other: Quantity): Quantity = Quantity(value - other.value)
    operator fun times(multiplier: Int): Quantity = Quantity(value * multiplier)
    operator fun unaryMinus(): Quantity = Quantity(-value)

    override operator fun compareTo(other: Quantity): Int = value.compareTo(other.value)

    val isZero: Boolean get() = value == 0
    val isPositive: Boolean get() = value > 0
    val isNegative: Boolean get() = value < 0

    fun absoluteValue(): Quantity = Quantity(abs(value))

    fun coerceAtLeast(minimumValue: Quantity): Quantity =
        if (value < minimumValue.value) minimumValue else this

    fun coerceAtMost(maximumValue: Quantity): Quantity =
        if (value > maximumValue.value) maximumValue else this

    override fun toString(): String = value.toString()

    companion object {
        val Zero: Quantity = Quantity(0)
        val One: Quantity = Quantity(1)
        fun parse(value: String): Quantity? = value.trim().toIntOrNull()?.let(::Quantity)
    }
}
