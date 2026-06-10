package app.devper.pharm.ui.designsystem

import app.devper.pharm.ui.format.ymdToMillis
import app.devper.pharm.ui.i18n.PharmStringsEn
import app.devper.pharm.ui.i18n.PharmStringsTh
import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class PharmCalendarTest {

    @Test
    fun june_2026_grid_starts_monday_with_one_leading_blank() {
        val weeks = CalendarMonth(2026, 6).weeks()
        assertNull(weeks.first()[0])
        assertEquals(LocalDate(2026, 6, 1), weeks.first()[1])
        assertEquals(LocalDate(2026, 6, 30), weeks.flatten().filterNotNull().last())
        assertEquals(30, weeks.flatten().filterNotNull().size)
        weeks.forEach { assertEquals(7, it.size) }
    }

    @Test
    fun february_leap_year_has_29_days() {
        assertEquals(29, CalendarMonth(2024, 2).weeks().flatten().filterNotNull().size)
        assertEquals(28, CalendarMonth(2026, 2).weeks().flatten().filterNotNull().size)
    }

    @Test
    fun previous_and_next_wrap_across_year_boundary() {
        assertEquals(CalendarMonth(2025, 12), CalendarMonth(2026, 1).previous())
        assertEquals(CalendarMonth(2027, 1), CalendarMonth(2026, 12).next())
    }

    @Test
    fun title_uses_buddhist_year_in_thai_and_ce_in_english() {
        val month = CalendarMonth(2026, 6)
        assertEquals("มิถุนายน 2569", month.title(PharmStringsTh))
        assertEquals("June 2026", month.title(PharmStringsEn))
    }

    @Test
    fun weekday_headers_are_sunday_first_and_locale_specific() {
        assertEquals(listOf("อา", "จ", "อ", "พ", "พฤ", "ศ", "ส"), weekdayHeaders(PharmStringsTh))
        assertEquals("Su", weekdayHeaders(PharmStringsEn).first())
    }

    @Test
    fun utc_millis_round_trip_matches_ymd_contract() {
        val millis = ymdToMillis("2026-06-10")
        assertEquals(LocalDate(2026, 6, 10), utcMillisToLocalDate(millis!!))
        assertEquals(millis, LocalDate(2026, 6, 10).toUtcStartOfDayMillis())
    }

    @Test
    fun every_month_has_a_nonblank_localized_name() {
        (1..12).forEach { m ->
            assertTrue(monthName(m, PharmStringsTh).isNotBlank())
            assertTrue(monthName(m, PharmStringsEn).isNotBlank())
        }
    }
}
