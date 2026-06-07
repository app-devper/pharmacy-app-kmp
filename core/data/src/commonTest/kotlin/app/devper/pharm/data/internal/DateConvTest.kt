package app.devper.pharm.data.internal

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class DateConvTest {

    @Test
    fun parseLocalDateOrNull_returns_typed_value_for_iso_date() {
        assertEquals(LocalDate(2026, 6, 7), "2026-06-07".parseLocalDateOrNull())
    }

    @Test
    fun parseLocalDateOrNull_returns_null_for_null() {
        assertNull((null as String?).parseLocalDateOrNull())
    }

    @Test
    fun parseLocalDateOrNull_returns_null_for_blank() {
        assertNull("".parseLocalDateOrNull())
        assertNull("   ".parseLocalDateOrNull())
    }

    @Test
    fun parseLocalDateOrNull_handles_datetime_string_from_backend() {
        assertEquals(LocalDate(2027, 6, 30), "2027-06-30T00:00:00Z".parseLocalDateOrNull())
    }

    @Test
    fun parseLocalDateOrNull_handles_datetime_string_with_offset() {
        assertEquals(LocalDate(2027, 6, 30), "2027-06-30T18:00:00+07:00".parseLocalDateOrNull())
    }

    @Test
    fun parseLocalDateOrNull_handles_datetime_string_with_fractional_seconds() {
        assertEquals(LocalDate(2027, 6, 30), "2027-06-30T12:34:56.789Z".parseLocalDateOrNull())
    }

    @Test
    fun parseLocalDateOrNull_returns_null_for_malformed() {
        assertNull("07/06/2026".parseLocalDateOrNull())
        assertNull("not-a-date".parseLocalDateOrNull())
        assertNull("2026-13-01".parseLocalDateOrNull())
    }

    @Test
    fun parseLocalDateTimeOrNull_naked_datetime_assumed_already_bangkok_local() {
        assertEquals(
            LocalDateTime(2026, 5, 17, 14, 42, 0),
            "2026-05-17T14:42:00".parseLocalDateTimeOrNull(),
        )
    }

    @Test
    fun parseLocalDateTimeOrNull_utc_z_converts_to_bangkok_plus_seven() {
        assertEquals(
            LocalDateTime(2026, 5, 17, 21, 42, 0),
            "2026-05-17T14:42:00Z".parseLocalDateTimeOrNull(),
        )
    }

    @Test
    fun parseLocalDateTimeOrNull_plus_seven_offset_stays_bangkok() {
        assertEquals(
            LocalDateTime(2026, 5, 17, 14, 42, 0),
            "2026-05-17T14:42:00+07:00".parseLocalDateTimeOrNull(),
        )
    }

    @Test
    fun parseLocalDateTimeOrNull_arbitrary_offset_normalises_to_bangkok() {
        assertEquals(
            LocalDateTime(2026, 5, 17, 14, 42, 0),
            "2026-05-17T03:42:00-04:00".parseLocalDateTimeOrNull(),
        )
    }

    @Test
    fun parseLocalDateTimeOrNull_z_at_midnight_utc_becomes_morning_bangkok() {
        assertEquals(
            LocalDateTime(2026, 5, 17, 7, 0, 0),
            "2026-05-17T00:00:00Z".parseLocalDateTimeOrNull(),
        )
    }

    @Test
    fun parseLocalDateTimeOrNull_z_late_evening_utc_rolls_to_next_day_bangkok() {
        assertEquals(
            LocalDateTime(2026, 5, 18, 6, 30, 0),
            "2026-05-17T23:30:00Z".parseLocalDateTimeOrNull(),
        )
    }

    @Test
    fun parseLocalDateTimeOrNull_returns_null_for_null() {
        assertNull((null as String?).parseLocalDateTimeOrNull())
    }

    @Test
    fun parseLocalDateTimeOrNull_returns_null_for_blank() {
        assertNull("".parseLocalDateTimeOrNull())
        assertNull("   ".parseLocalDateTimeOrNull())
    }

    @Test
    fun parseLocalDateTimeOrNull_returns_null_for_malformed() {
        assertNull("not-a-datetime".parseLocalDateTimeOrNull())
        assertNull("2026-05-17 14:42:00".parseLocalDateTimeOrNull())
    }

    @Test
    fun parseLocalDateOrEpoch_falls_back_to_1970() {
        assertEquals(LocalDate(1970, 1, 1), "garbage".parseLocalDateOrEpoch())
    }

    @Test
    fun parseLocalDateTimeOrEpoch_falls_back_to_1970() {
        assertEquals(LocalDateTime(1970, 1, 1, 0, 0, 0), "garbage".parseLocalDateTimeOrEpoch())
    }

    @Test
    fun toIso_emits_plain_iso_for_LocalDate_no_offset_no_z() {
        assertEquals("2026-06-07", LocalDate(2026, 6, 7).toIso())
    }

    @Test
    fun toIso_emits_plain_iso_for_LocalDateTime_no_offset_no_z() {
        assertEquals("2026-05-17T14:42:30", LocalDateTime(2026, 5, 17, 14, 42, 30).toIso())
    }

    @Test
    fun toIso_round_trip_LocalDateTime_with_seconds() {
        val original = LocalDateTime(2026, 5, 17, 14, 42, 30)
        assertEquals(original, original.toIso().parseLocalDateTimeOrNull())
    }
}
