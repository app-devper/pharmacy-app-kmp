package app.devper.pharm.ui.format

import kotlinx.datetime.toLocalDateTime
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

    @Test
    fun toLocalDateOrNull_parses_iso_date() {
        assertEquals(kotlinx.datetime.LocalDate(2026, 6, 7), "2026-06-07".toLocalDateOrNull())
    }

    @Test
    fun toLocalDateOrNull_returns_null_for_blank() {
        assertNull("".toLocalDateOrNull())
    }

    @Test
    fun toLocalDateOrNull_returns_null_for_thai_dd_mm_yyyy_format() {
        assertNull("07/06/2026".toLocalDateOrNull())
    }

    @Test
    fun toLocalDateOrNull_returns_null_for_garbage() {
        assertNull("not-a-date".toLocalDateOrNull())
    }

    @Test
    fun toLocalDateOrNull_returns_null_for_invalid_month() {
        assertNull("2026-13-01".toLocalDateOrNull())
    }

    @Test
    fun isoDateTimeToBuddhist_naked_datetime_assumed_already_bangkok_local() {
        assertEquals("17/05/2569 14:42", isoDateTimeToBuddhist("2026-05-17T14:42:00"))
    }

    @Test
    fun isoDateTimeToBuddhist_utc_z_converts_to_bangkok_plus_seven() {
        assertEquals("17/05/2569 21:42", isoDateTimeToBuddhist("2026-05-17T14:42:00Z"))
    }

    @Test
    fun isoDateTimeToBuddhist_plus_seven_offset_stays_bangkok() {
        assertEquals("17/05/2569 14:42", isoDateTimeToBuddhist("2026-05-17T14:42:00+07:00"))
    }

    @Test
    fun isoDateTimeToBuddhist_z_late_evening_utc_rolls_to_next_day_bangkok() {
        assertEquals("18/05/2569 06:30", isoDateTimeToBuddhist("2026-05-17T23:30:00Z"))
    }

    @Test
    fun isoDateTimeToBuddhist_returns_input_for_unparseable() {
        assertEquals("not-a-datetime", isoDateTimeToBuddhist("not-a-datetime"))
    }

    @Test
    fun isoDateTimeToBuddhist_returns_blank_for_blank() {
        assertEquals("", isoDateTimeToBuddhist(""))
    }

    @Test
    fun toLocalDateOrNull_handles_datetime_string_from_backend() {
        assertEquals(kotlinx.datetime.LocalDate(2027, 6, 30), "2027-06-30T00:00:00Z".toLocalDateOrNull())
    }

    @Test
    fun isoDateToBuddhist_handles_datetime_string_from_backend() {
        assertEquals("30/06/2570", isoDateToBuddhist("2027-06-30T00:00:00Z"))
    }

    @OptIn(kotlin.time.ExperimentalTime::class)
    @Test
    fun ymdToMillis_produces_utc_midnight_for_m3_datepicker_roundtrip() {
        val ms = ymdToMillis("2026-05-17") ?: error("parse")
        val date = kotlin.time.Instant.fromEpochMilliseconds(ms)
            .toLocalDateTime(kotlinx.datetime.TimeZone.UTC).date
        assertEquals(kotlinx.datetime.LocalDate(2026, 5, 17), date)
    }

    @OptIn(kotlin.time.ExperimentalTime::class)
    @Test
    fun millisToYmd_decodes_utc_midnight_back_to_same_ymd() {
        val ms = ymdToMillis("2026-05-17") ?: error("parse")
        assertEquals("2026-05-17", millisToYmd(ms))
    }

    @OptIn(kotlin.time.ExperimentalTime::class)
    @Test
    fun ymdToMillis_returns_zero_offset_relative_to_utc_epoch() {
        // sanity: 1970-01-01 UTC midnight = 0
        assertEquals(0L, ymdToMillis("1970-01-01"))
    }
}
