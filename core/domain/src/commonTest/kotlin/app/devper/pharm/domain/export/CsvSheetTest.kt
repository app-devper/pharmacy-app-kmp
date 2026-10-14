package app.devper.pharm.domain.export

import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals

class CsvSheetTest {

    @Test
    fun a_period_names_the_file_the_same_way_for_every_export() {
        val may1 = LocalDate(2026, 5, 1)
        val may31 = LocalDate(2026, 5, 31)
        assertEquals("all", periodTag(null, null))
        assertEquals("2026-05-01", periodTag(may1, may1))
        assertEquals("2026-05-01_2026-05-31", periodTag(may1, may31))
        assertEquals("any_2026-05-31", periodTag(null, may31))
        assertEquals("2026-05-01_any", periodTag(may1, null))
    }

    @Test
    fun a_name_becomes_a_safe_file_tag() {
        assertEquals("paracetamol-500mg", nameTag("  Paracetamol 500mg! "))
        assertEquals("", nameTag("   "))
        assertEquals(5, nameTag("abcdefgh", maxLength = 5).length)
    }

    @Test
    fun money_and_percent_round_half_away_from_zero_to_satang() {
        assertEquals("12.35", csvMoney(12.345))
        assertEquals("-12.35", csvMoney(-12.345))
        assertEquals("0.00", csvMoney(0.0))
        assertEquals("33.33%", csvPercent(33.333))
    }
}
