package app.devper.pharm.presentation.reports.internal

import kotlin.time.ExperimentalTime
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.toInstant
import app.devper.pharm.ui.format.todayLocalDate

@OptIn(ExperimentalTime::class)
@Suppress("UNUSED_PARAMETER")
internal fun LocalDate.toStartOfDayMillis(tz: TimeZone): Long =
    LocalDateTime(this, LocalTime(0, 0))
        .toInstant(TimeZone.UTC)
        .toEpochMilliseconds()

enum class ProfitQuickPeriod(val label: String) {
    Today("Today"),
    ThisWeek("This week"),
    ThisMonth("This month"),
    LastMonth("Last month"),
}

fun ProfitQuickPeriod.localized(s: app.devper.pharm.ui.i18n.PharmStrings): String = when (this) {
    ProfitQuickPeriod.Today -> s.reportsRangeToday
    ProfitQuickPeriod.ThisWeek -> s.reportsRangeThisWeek
    ProfitQuickPeriod.ThisMonth -> s.reportsRangeThisMonth
    ProfitQuickPeriod.LastMonth -> s.reportsRangeLastMonth
}

internal data class ProfitDateRange(val fromMillis: Long, val toMillis: Long)

internal fun ProfitQuickPeriod.resolve(tz: TimeZone): ProfitDateRange {
    val today = todayLocalDate(tz)
    return when (this) {
        ProfitQuickPeriod.Today -> ProfitDateRange(today.toStartOfDayMillis(tz), today.toStartOfDayMillis(tz))
        ProfitQuickPeriod.ThisWeek -> {
            val daysFromMonday = ((today.dayOfWeek.ordinal - DayOfWeek.MONDAY.ordinal) + 7) % 7
            val start = today.minus(daysFromMonday, DateTimeUnit.DAY)
            ProfitDateRange(start.toStartOfDayMillis(tz), today.toStartOfDayMillis(tz))
        }
        ProfitQuickPeriod.ThisMonth -> {
            val start = LocalDate(today.year, today.month, 1)
            ProfitDateRange(start.toStartOfDayMillis(tz), today.toStartOfDayMillis(tz))
        }
        ProfitQuickPeriod.LastMonth -> {
            val firstOfThisMonth = LocalDate(today.year, today.month, 1)
            val lastOfPrev = firstOfThisMonth.minus(1, DateTimeUnit.DAY)
            val firstOfPrev = LocalDate(lastOfPrev.year, lastOfPrev.month, 1)
            ProfitDateRange(firstOfPrev.toStartOfDayMillis(tz), lastOfPrev.toStartOfDayMillis(tz))
        }
    }
}
