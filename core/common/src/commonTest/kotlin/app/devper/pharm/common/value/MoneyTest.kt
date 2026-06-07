package app.devper.pharm.common.value

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class MoneyTest {

    @Test
    fun zero_is_singleton_value() {
        assertEquals(Money(0.0), Money.Zero)
    }

    @Test
    fun plus_sums_amounts() {
        assertEquals(Money(7.5), Money(2.5) + Money(5.0))
    }

    @Test
    fun minus_subtracts_amounts() {
        assertEquals(Money(2.5), Money(7.5) - Money(5.0))
    }

    @Test
    fun times_int_scales_amount() {
        assertEquals(Money(30.0), Money(10.0) * 3)
    }

    @Test
    fun times_double_scales_amount() {
        assertEquals(Money(7.5), Money(5.0) * 1.5)
    }

    @Test
    fun unary_minus_negates_amount() {
        assertEquals(Money(-5.0), -Money(5.0))
    }

    @Test
    fun comparison_orders_by_amount() {
        assertTrue(Money(1.0) < Money(2.0))
        assertTrue(Money(5.0) > Money(2.0))
        assertEquals(0, Money(3.0).compareTo(Money(3.0)))
    }

    @Test
    fun is_zero_positive_negative_predicates() {
        assertTrue(Money.Zero.isZero)
        assertFalse(Money(1.0).isZero)
        assertTrue(Money(1.0).isPositive)
        assertFalse(Money.Zero.isPositive)
        assertTrue(Money(-1.0).isNegative)
        assertFalse(Money.Zero.isNegative)
    }

    @Test
    fun absoluteValue_returns_non_negative() {
        assertEquals(Money(5.0), Money(-5.0).absoluteValue())
        assertEquals(Money(5.0), Money(5.0).absoluteValue())
    }

    @Test
    fun format_uses_two_decimals_by_default() {
        assertEquals("12.50", Money(12.5).format())
        assertEquals("0.00", Money.Zero.format())
        assertEquals("-7.30", Money(-7.30).format())
    }

    @Test
    fun format_pads_fractional_digits() {
        assertEquals("12.50", Money(12.5).format())
        assertEquals("12.00", Money(12.0).format())
        assertEquals("0.05", Money(0.05).format())
    }

    @Test
    fun format_rounds_half_away_from_zero_to_decimals() {
        assertEquals("12.46", Money(12.4567).format(2))
        assertEquals("12.5", Money(12.46).format(1))
    }

    @Test
    fun format_zero_decimals_drops_fractional() {
        assertEquals("13", Money(12.6).format(0))
    }

    @Test
    fun parse_returns_money_for_numeric_string() {
        assertEquals(Money(12.5), Money.parse("12.5"))
        assertEquals(Money(12.5), Money.parse(" 12.5 "))
    }

    @Test
    fun parse_returns_null_for_invalid_string() {
        assertNull(Money.parse(""))
        assertNull(Money.parse("abc"))
        assertNull(Money.parse("1.2.3"))
    }
}
