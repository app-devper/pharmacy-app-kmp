package app.devper.pharm.common.value

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class QuantityTest {

    @Test
    fun zero_and_one_singletons() {
        assertEquals(Quantity(0), Quantity.Zero)
        assertEquals(Quantity(1), Quantity.One)
    }

    @Test
    fun plus_minus_times() {
        assertEquals(Quantity(7), Quantity(3) + Quantity(4))
        assertEquals(Quantity(2), Quantity(7) - Quantity(5))
        assertEquals(Quantity(15), Quantity(5) * 3)
    }

    @Test
    fun unary_minus_negates() {
        assertEquals(Quantity(-5), -Quantity(5))
    }

    @Test
    fun comparison_orders_by_value() {
        assertTrue(Quantity(1) < Quantity(2))
        assertTrue(Quantity(5) > Quantity(2))
        assertEquals(0, Quantity(3).compareTo(Quantity(3)))
    }

    @Test
    fun is_zero_positive_negative_predicates() {
        assertTrue(Quantity.Zero.isZero)
        assertFalse(Quantity.One.isZero)
        assertTrue(Quantity.One.isPositive)
        assertFalse(Quantity.Zero.isPositive)
        assertTrue(Quantity(-1).isNegative)
        assertFalse(Quantity.Zero.isNegative)
    }

    @Test
    fun absoluteValue_returns_non_negative() {
        assertEquals(Quantity(5), Quantity(-5).absoluteValue())
        assertEquals(Quantity(5), Quantity(5).absoluteValue())
    }

    @Test
    fun coerceAtLeast_clamps_to_floor() {
        assertEquals(Quantity(5), Quantity(3).coerceAtLeast(Quantity(5)))
        assertEquals(Quantity(7), Quantity(7).coerceAtLeast(Quantity(5)))
    }

    @Test
    fun coerceAtMost_clamps_to_ceiling() {
        assertEquals(Quantity(5), Quantity(7).coerceAtMost(Quantity(5)))
        assertEquals(Quantity(3), Quantity(3).coerceAtMost(Quantity(5)))
    }

    @Test
    fun parse_returns_quantity_for_numeric_string() {
        assertEquals(Quantity(42), Quantity.parse("42"))
        assertEquals(Quantity(42), Quantity.parse(" 42 "))
        assertEquals(Quantity(-3), Quantity.parse("-3"))
    }

    @Test
    fun parse_returns_null_for_invalid_string() {
        assertNull(Quantity.parse(""))
        assertNull(Quantity.parse("abc"))
        assertNull(Quantity.parse("1.5"))
    }

    @Test
    fun toString_returns_underlying_int() {
        assertEquals("42", Quantity(42).toString())
        assertEquals("-3", Quantity(-3).toString())
    }
}
