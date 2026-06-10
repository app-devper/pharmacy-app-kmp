package app.devper.pharm.ui.designsystem

import app.devper.pharm.ui.i18n.PharmStrings
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month
import kotlinx.datetime.plus

data class CalendarMonth(val year: Int, val month: Int) {

    fun previous(): CalendarMonth =
        if (month == 1) CalendarMonth(year - 1, 12) else CalendarMonth(year, month - 1)

    fun next(): CalendarMonth =
        if (month == 12) CalendarMonth(year + 1, 1) else CalendarMonth(year, month + 1)

    fun contains(date: LocalDate): Boolean = date.year == year && date.monthNumber == month

    companion object {
        fun of(date: LocalDate): CalendarMonth = CalendarMonth(date.year, date.monthNumber)
    }
}

fun CalendarMonth.weeks(): List<List<LocalDate?>> {
    val first = LocalDate(year, month, 1)
    val leadingBlanks = first.dayOfWeek.sundayFirstIndex()
    val daysInMonth = (first.plus(1, DateTimeUnit.MONTH).toEpochDays() - first.toEpochDays()).toInt()
    val cells: List<LocalDate?> =
        List(leadingBlanks) { null } + (1..daysInMonth).map { day -> LocalDate(year, month, day) }
    return cells.chunked(7).map { week -> week + List(7 - week.size) { null } }
}

private fun DayOfWeek.sundayFirstIndex(): Int = when (this) {
    DayOfWeek.SUNDAY -> 0
    DayOfWeek.MONDAY -> 1
    DayOfWeek.TUESDAY -> 2
    DayOfWeek.WEDNESDAY -> 3
    DayOfWeek.THURSDAY -> 4
    DayOfWeek.FRIDAY -> 5
    DayOfWeek.SATURDAY -> 6
    else -> 0
}

fun CalendarMonth.title(s: PharmStrings): String = "${monthName(month, s)} ${s.calendarYear(year)}"

fun monthName(month: Int, s: PharmStrings): String = when (Month(month)) {
    Month.JANUARY -> s.calendarMonthJanuary
    Month.FEBRUARY -> s.calendarMonthFebruary
    Month.MARCH -> s.calendarMonthMarch
    Month.APRIL -> s.calendarMonthApril
    Month.MAY -> s.calendarMonthMay
    Month.JUNE -> s.calendarMonthJune
    Month.JULY -> s.calendarMonthJuly
    Month.AUGUST -> s.calendarMonthAugust
    Month.SEPTEMBER -> s.calendarMonthSeptember
    Month.OCTOBER -> s.calendarMonthOctober
    Month.NOVEMBER -> s.calendarMonthNovember
    Month.DECEMBER -> s.calendarMonthDecember
    else -> ""
}

fun weekdayHeaders(s: PharmStrings): List<String> = listOf(
    s.calendarWeekdaySun,
    s.calendarWeekdayMon,
    s.calendarWeekdayTue,
    s.calendarWeekdayWed,
    s.calendarWeekdayThu,
    s.calendarWeekdayFri,
    s.calendarWeekdaySat,
)
