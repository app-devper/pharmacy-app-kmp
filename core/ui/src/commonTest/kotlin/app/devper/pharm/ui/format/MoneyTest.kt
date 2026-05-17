package app.devper.pharm.ui.format

import kotlin.test.Test
import kotlin.test.assertEquals

class MoneyTest {

    @Test
    fun zero_formats_with_two_decimals() {
        assertEquals("0.00", formatBaht(0.0))
    }

    @Test
    fun whole_numbers_show_two_decimals() {
        assertEquals("125.00", formatBaht(125.0))
    }

    @Test
    fun thousand_separators_are_inserted() {
        assertEquals("1,234.50", formatBaht(1234.5))
        assertEquals("1,234,567.89", formatBaht(1234567.89))
    }

    @Test
    fun negative_values_get_leading_minus() {
        assertEquals("-99.50", formatBaht(-99.50))
        assertEquals("-1,000.00", formatBaht(-1000.0))
    }

    @Test
    fun rounding_to_nearest_cent_at_half() {
        assertEquals("0.46", formatBaht(0.455))
        assertEquals("1.99", formatBaht(1.985 - 0.0))
    }

    @Test
    fun very_small_positive_rounds_to_zero() {
        assertEquals("0.00", formatBaht(0.001))
    }

    @Test
    fun currency_variant_adds_baht_glyph() {
        assertEquals("฿0.00", formatBahtCurrency(0.0))
        assertEquals("฿1,234.50", formatBahtCurrency(1234.5))
    }

    @Test
    fun currency_variant_handles_negatives() {
        assertEquals("฿-99.50", formatBahtCurrency(-99.50))
    }
}
