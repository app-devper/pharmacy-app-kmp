package app.devper.pharm.ui.theme

import app.devper.pharm.ui.format.formatBahtCurrency
import kotlin.test.Test
import kotlin.test.assertEquals

class FmtBahtTest {

    @Test
    fun whole_amounts_keep_two_decimals() {
        assertEquals("฿0.00", fmtBaht(0.0))
        assertEquals("฿1,000.00", fmtBaht(1000.0))
    }

    @Test
    fun fractional_double_shows_two_decimals() {
        assertEquals("฿1,234.50", fmtBaht(1234.5))
    }

    @Test
    fun int_overload_treats_as_whole_baht() {
        assertEquals("฿42.00", fmtBaht(42))
        assertEquals("฿12,345.00", fmtBaht(12345))
    }

    @Test
    fun negative_values_keep_sign_after_glyph() {
        assertEquals("฿-99.50", fmtBaht(-99.50))
        assertEquals("฿-1,000.00", fmtBaht(-1000))
    }

    @Test
    fun fractional_rounding_carries_into_whole() {
        assertEquals("฿6.00", fmtBaht(5.996))
        assertEquals("฿2.00", fmtBaht(1.999))
        assertEquals("฿1,000.00", fmtBaht(999.999))
        assertEquals("฿-6.00", fmtBaht(-5.996))
    }

    @Test
    fun the_screen_helper_and_the_receipt_helper_agree() {
        listOf(0.0, 1000.0, 1234.5, -99.5, 5.996).forEach { value ->
            assertEquals(formatBahtCurrency(value), fmtBaht(value))
        }
    }
}
