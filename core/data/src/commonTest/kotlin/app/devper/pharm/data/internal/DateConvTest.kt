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
    fun parseLocalDateOrNull_returns_null_for_malformed() {
        assertNull("07/06/2026".parseLocalDateOrNull())
        assertNull("not-a-date".parseLocalDateOrNull())
        assertNull("2026-13-01".parseLocalDateOrNull())
    }

    @Test
    fun parseLocalDateTimeOrNull_returns_typed_value_for_iso_datetime() {
        assertEquals(
            LocalDateTime(2026, 5, 17, 14, 42, 0),
            "2026-05-17T14:42:00".parseLocalDateTimeOrNull(),
        )
    }

    @Test
    fun parseLocalDateTimeOrNull_strips_trailing_Z() {
        assertEquals(
            LocalDateTime(2026, 5, 17, 14, 42, 0),
            "2026-05-17T14:42:00Z".parseLocalDateTimeOrNull(),
        )
    }

    @Test
    fun parseLocalDateTimeOrNull_strips_plus_offset() {
        assertEquals(
            LocalDateTime(2026, 5, 17, 14, 42, 0),
            "2026-05-17T14:42:00+07:00".parseLocalDateTimeOrNull(),
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
