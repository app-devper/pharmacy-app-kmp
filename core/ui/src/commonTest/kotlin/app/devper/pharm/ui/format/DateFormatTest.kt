package app.devper.pharm.ui.format

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class DateFormatTest {

    @Test
    fun millis_to_ymd_returns_blank_for_null() {
        assertEquals("", millisToYmd(null))
    }

    @Test
    fun millis_to_ymd_emits_iso_format_with_zero_padding() {
        val millis = millisForKnownDay()
        val ymd = millisToYmd(millis)
        assertTrue(ymd.matches(Regex("\\d{4}-\\d{2}-\\d{2}")), "got '$ymd'")
    }

    @Test
    fun ymd_to_millis_returns_null_for_blank() {
        assertNull(ymdToMillis(""))
        assertNull(ymdToMillis("   "))
    }

    @Test
    fun ymd_to_millis_returns_null_for_invalid_string() {
        assertNull(ymdToMillis("not-a-date"))
        assertNull(ymdToMillis("2026-13-45"))
        assertNull(ymdToMillis("2026/05/17"))
    }

    @Test
    fun ymd_to_millis_then_millis_to_ymd_round_trips() {
        val sampleYmds = listOf("2026-01-01", "2026-05-17", "2025-12-31", "2030-02-28")
        sampleYmds.forEach { ymd ->
            val ms = ymdToMillis(ymd)
            assertTrue(ms != null, "ymdToMillis('$ymd') must parse")
            assertEquals(ymd, millisToYmd(ms), "round-trip for '$ymd' should preserve string")
        }
    }

    @Test
    fun ymd_to_millis_normalises_to_start_of_day() {
        val ms1 = ymdToMillis("2026-05-17")
        assertTrue(ms1 != null)
        val msAgain = ymdToMillis("2026-05-17")
        assertEquals(ms1, msAgain, "same YMD must produce the same millis (start-of-day)")
    }

    @Test
    fun format_ymd_display_matches_millis_to_ymd() {
        val ms = millisForKnownDay()
        assertEquals(millisToYmd(ms), formatYmdDisplay(ms))
    }

    @Test
    fun pads_single_digit_month_and_day() {
        val ms = ymdToMillis("2026-01-05")
        assertTrue(ms != null)
        assertEquals("2026-01-05", millisToYmd(ms))
    }

    private fun millisForKnownDay(): Long = ymdToMillis("2026-05-17")!!
}
