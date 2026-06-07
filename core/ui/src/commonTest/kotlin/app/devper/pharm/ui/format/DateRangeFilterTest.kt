package app.devper.pharm.ui.format

import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNull

private val TZ = TimeZone.of("Asia/Bangkok")

class DateRangeFilterTest {

    @Test
    fun defaults_to_empty_strings_and_asia_bangkok() {
        val r = DateRangeFilter()
        assertEquals("", r.from)
        assertEquals("", r.to)
        assertEquals(TZ, r.tz)
    }

    @Test
    fun fromDate_parses_iso_yyyy_mm_dd() {
        val r = DateRangeFilter(from = "2026-06-07", tz = TZ)
        assertEquals(LocalDate(2026, 6, 7), r.fromDate)
    }

    @Test
    fun fromDate_returns_null_when_blank_or_unparseable() {
        assertNull(DateRangeFilter(from = "", tz = TZ).fromDate)
        assertNull(DateRangeFilter(from = "07/06/2026", tz = TZ).fromDate)
        assertNull(DateRangeFilter(from = "garbage", tz = TZ).fromDate)
    }

    @Test
    fun withFromMillis_round_trip() {
        val r = DateRangeFilter(tz = TZ)
        val midnightMillis = r.withFrom("2026-06-07").fromMillis
        val rebuilt = r.withFromMillis(midnightMillis)
        assertEquals("2026-06-07", rebuilt.from)
    }

    @Test
    fun withToMillis_round_trip() {
        val r = DateRangeFilter(tz = TZ)
        val midnightMillis = r.withTo("2026-06-30").toMillis
        val rebuilt = r.withToMillis(midnightMillis)
        assertEquals("2026-06-30", rebuilt.to)
    }

    @Test
    fun withFromMillis_with_null_clears() {
        val r = DateRangeFilter(from = "2026-06-07", tz = TZ)
        val cleared = r.withFromMillis(null)
        assertEquals("", cleared.from)
    }

    @Test
    fun toDate_returns_null_when_to_is_blank() {
        assertNull(DateRangeFilter(tz = TZ).toDate)
    }

    @Test
    fun withFrom_does_not_modify_original_immutable_copy() {
        val r = DateRangeFilter(tz = TZ)
        val updated = r.withFrom("2026-06-07")
        assertEquals("", r.from)
        assertEquals("2026-06-07", updated.from)
        assertNotEquals(r, updated)
    }
}
