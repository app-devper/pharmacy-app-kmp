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
    fun a_picked_range_round_trips_through_the_field() {
        val r = DateRangeFilter(from = "2026-06-07", to = "2026-06-30", tz = TZ)
        val rebuilt = DateRangeFilter(tz = TZ).withRange(r.range)
        assertEquals("2026-06-07", rebuilt.from)
        assertEquals("2026-06-30", rebuilt.to)
    }

    @Test
    fun clearing_one_side_of_the_range_clears_only_that_side() {
        val r = DateRangeFilter(from = "2026-06-07", to = "2026-06-30", tz = TZ)
        val cleared = r.withRange(r.range.copy(fromMillis = null))
        assertEquals("", cleared.from)
        assertEquals("2026-06-30", cleared.to)
    }

    @Test
    fun quick_periods_resolve_from_today() {
        val today = LocalDate(2026, 10, 8)
        val r = DateRangeFilter(tz = TZ)
        assertEquals("2026-10-08" to "2026-10-08", r.withPeriod(QuickPeriod.Today, today).let { it.from to it.to })
        assertEquals("2026-10-02" to "2026-10-08", r.withPeriod(QuickPeriod.Last7Days, today).let { it.from to it.to })
        assertEquals("2026-10-05" to "2026-10-08", r.withPeriod(QuickPeriod.ThisWeek, today).let { it.from to it.to })
        assertEquals("2026-10-01" to "2026-10-08", r.withPeriod(QuickPeriod.ThisMonth, today).let { it.from to it.to })
        assertEquals("2026-09-01" to "2026-09-30", r.withPeriod(QuickPeriod.LastMonth, today).let { it.from to it.to })
    }

    @Test
    fun last_month_in_january_is_december_of_the_previous_year() {
        val r = DateRangeFilter(tz = TZ).withPeriod(QuickPeriod.LastMonth, LocalDate(2027, 1, 15))
        assertEquals("2026-12-01" to "2026-12-31", r.from to r.to)
    }

    @Test
    fun the_active_period_is_the_offered_one_the_range_matches() {
        val today = LocalDate(2026, 10, 8)
        val offered = listOf(QuickPeriod.Today, QuickPeriod.Last7Days, QuickPeriod.ThisMonth)
        val r = DateRangeFilter(tz = TZ).withPeriod(QuickPeriod.Last7Days, today)
        assertEquals(QuickPeriod.Last7Days, r.activePeriod(offered, today))
        assertNull(r.withFrom("2026-10-03").activePeriod(offered, today))
        assertNull(r.withPeriod(QuickPeriod.LastMonth, today).activePeriod(offered, today))
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
