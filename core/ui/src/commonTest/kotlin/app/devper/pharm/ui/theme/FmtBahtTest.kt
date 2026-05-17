package app.devper.pharm.ui.theme

import kotlin.test.Test
import kotlin.test.assertEquals

class FmtBahtTest {

    @Test
    fun integer_double_drops_decimals() {
        assertEquals("฿0", fmtBaht(0.0))
        assertEquals("฿1,000", fmtBaht(1000.0))
    }

    @Test
    fun fractional_double_shows_two_decimals() {
        assertEquals("฿1,234.50", fmtBaht(1234.5))
    }

    @Test
    fun int_overload_treats_as_whole_baht() {
        assertEquals("฿42", fmtBaht(42))
        assertEquals("฿12,345", fmtBaht(12345))
    }

    @Test
    fun negative_values_keep_sign_after_glyph() {
        assertEquals("฿-99.50", fmtBaht(-99.50))
        assertEquals("฿-1,000", fmtBaht(-1000))
    }
}
