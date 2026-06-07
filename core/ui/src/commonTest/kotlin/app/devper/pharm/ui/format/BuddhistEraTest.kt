package app.devper.pharm.ui.format

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.ExperimentalTime

private val TZ = TimeZone.of("Asia/Bangkok")

@OptIn(ExperimentalTime::class)
class BuddhistEraTest {

    @Test
    fun toBuddhistEraDisplay_formats_year_plus_543() {
        val date = LocalDate(2026, 6, 7)
        assertEquals("07/06/2569", toBuddhistEraDisplay(date))
    }

    @Test
    fun toBuddhistEraDisplay_pads_single_digit_day_and_month() {
        val date = LocalDate(2026, 1, 3)
        assertEquals("03/01/2569", toBuddhistEraDisplay(date))
    }

    @Test
    fun toBuddhistEraDisplay_handles_leap_day() {
        val date = LocalDate(2028, 2, 29)
        assertEquals("29/02/2571", toBuddhistEraDisplay(date))
    }

    @Test
    fun millisToBuddhistDisplay_returns_empty_for_null() {
        assertEquals("", millisToBuddhistDisplay(null, TZ))
    }

    @Test
    fun millisToBuddhistDisplay_uses_given_timezone() {
        val midnightBangkokMillis = LocalDateTime(2026, 6, 7, 0, 0)
            .toInstant(TZ)
            .toEpochMilliseconds()
        assertEquals("07/06/2569", millisToBuddhistDisplay(midnightBangkokMillis, TZ))
    }

    @Test
    fun millisToBuddhistDisplayWithTime_appends_24h_clock() {
        val millis = LocalDateTime(2026, 6, 7, 14, 30)
            .toInstant(TZ)
            .toEpochMilliseconds()
        assertEquals("07/06/2569 14:30", millisToBuddhistDisplayWithTime(millis, TZ))
    }

    @Test
    fun millisToBuddhistDisplayWithTime_pads_single_digit_hour() {
        val millis = LocalDateTime(2026, 6, 7, 1, 5)
            .toInstant(TZ)
            .toEpochMilliseconds()
        assertEquals("07/06/2569 01:05", millisToBuddhistDisplayWithTime(millis, TZ))
    }

    @Test
    fun isoDateToBuddhist_parses_iso_date() {
        assertEquals("31/12/2569", isoDateToBuddhist("2026-12-31"))
    }

    @Test
    fun isoDateToBuddhist_returns_empty_for_blank() {
        assertEquals("", isoDateToBuddhist(""))
    }

    @Test
    fun isoDateToBuddhist_returns_original_on_parse_failure() {
        assertEquals("not-a-date", isoDateToBuddhist("not-a-date"))
    }

    @Test
    fun isoDateToBuddhist_accepts_datetime_string_and_keeps_date_part() {
        assertEquals("30/06/2570", isoDateToBuddhist("2027-06-30T00:00:00Z"))
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
    fun isoDateTimeToBuddhist_returns_empty_for_blank() {
        assertEquals("", isoDateTimeToBuddhist(""))
    }

    @Test
    fun isoDateTimeToBuddhist_returns_original_on_parse_failure() {
        assertEquals("garbage", isoDateTimeToBuddhist("garbage"))
    }
}
