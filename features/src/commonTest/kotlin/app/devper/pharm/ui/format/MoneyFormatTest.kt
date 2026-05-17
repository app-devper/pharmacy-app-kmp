package app.devper.pharm.ui.format

import kotlin.test.Test
import kotlin.test.assertEquals

class MoneyFormatTest {

    @Test
    fun whole_numbers_format_with_two_decimals_and_grouping() {
        assertEquals("0.00", formatBaht(0.0))
        assertEquals("1.00", formatBaht(1.0))
        assertEquals("100.00", formatBaht(100.0))
        assertEquals("1,000.00", formatBaht(1000.0))
        assertEquals("1,234,567.00", formatBaht(1234567.0))
    }

    @Test
    fun fractional_values_are_rounded_half_away_from_zero() {

        assertEquals("0.01", formatBaht(0.005))
        assertEquals("0.00", formatBaht(0.004))
        assertEquals("12.35", formatBaht(12.345))
    }

    @Test
    fun negatives_keep_the_sign_on_the_integer_part() {
        assertEquals("-12.34", formatBaht(-12.34))
        assertEquals("-1,000.00", formatBaht(-1000.0))

        assertEquals("-0.01", formatBaht(-0.005))
    }

    @Test
    fun currency_helper_prefixes_baht_symbol() {
        assertEquals("฿1,234.56", formatBahtCurrency(1234.56))
        assertEquals("฿0.00", formatBahtCurrency(0.0))
        assertEquals("฿-100.00", formatBahtCurrency(-100.0))
    }
}
