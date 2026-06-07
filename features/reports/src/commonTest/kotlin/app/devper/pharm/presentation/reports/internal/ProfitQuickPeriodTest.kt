package app.devper.pharm.presentation.reports.internal

import kotlinx.datetime.TimeZone
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

private val TZ = TimeZone.of("Asia/Bangkok")

class ProfitQuickPeriodTest {

    @Test
    fun every_period_has_thai_label() {
        ProfitQuickPeriod.entries.forEach { period ->
            assertTrue(period.label.isNotBlank(), "$period must have a non-blank label")
        }
    }

    @Test
    fun every_period_resolves_to_non_negative_range() {
        ProfitQuickPeriod.entries.forEach { period ->
            val range = period.resolve(TZ)
            assertTrue(range.fromMillis >= 0L, "$period fromMillis must be non-negative")
            assertTrue(range.toMillis >= 0L, "$period toMillis must be non-negative")
        }
    }

    @Test
    fun today_resolves_to_a_single_day_window() {
        val range = ProfitQuickPeriod.Today.resolve(TZ)
        assertEquals(range.fromMillis, range.toMillis, "Today.from == Today.to (start-of-day boundary)")
    }

    @Test
    fun this_week_starts_no_later_than_today() {
        val range = ProfitQuickPeriod.ThisWeek.resolve(TZ)
        assertTrue(range.fromMillis <= range.toMillis, "ThisWeek.from must be <= to")
    }

    @Test
    fun this_week_spans_at_most_seven_days() {
        val range = ProfitQuickPeriod.ThisWeek.resolve(TZ)
        val sevenDaysMs = 7L * 24L * 60L * 60L * 1000L
        assertTrue(
            range.toMillis - range.fromMillis < sevenDaysMs,
            "ThisWeek span must be < 7 days (Monday-to-today inclusive)",
        )
    }

    @Test
    fun this_month_starts_no_later_than_today() {
        val range = ProfitQuickPeriod.ThisMonth.resolve(TZ)
        assertTrue(range.fromMillis <= range.toMillis, "ThisMonth.from must be <= to")
    }

    @Test
    fun this_month_spans_at_most_31_days() {
        val range = ProfitQuickPeriod.ThisMonth.resolve(TZ)
        val thirtyOneDaysMs = 31L * 24L * 60L * 60L * 1000L
        assertTrue(
            range.toMillis - range.fromMillis < thirtyOneDaysMs,
            "ThisMonth span must be < 31 days",
        )
    }

    @Test
    fun last_month_ends_before_this_month_starts() {
        val lastMonth = ProfitQuickPeriod.LastMonth.resolve(TZ)
        val thisMonth = ProfitQuickPeriod.ThisMonth.resolve(TZ)
        assertTrue(
            lastMonth.toMillis < thisMonth.fromMillis,
            "LastMonth.to must end strictly before ThisMonth.from",
        )
    }

    @Test
    fun last_month_spans_at_least_28_days_and_at_most_31() {
        val range = ProfitQuickPeriod.LastMonth.resolve(TZ)
        val twentyEightDaysMs = 28L * 24L * 60L * 60L * 1000L
        val thirtyOneDaysMs = 31L * 24L * 60L * 60L * 1000L
        val spanMs = range.toMillis - range.fromMillis
        assertTrue(spanMs in twentyEightDaysMs..thirtyOneDaysMs, "LastMonth span (got $spanMs ms) must be 28-31 days")
    }
}
