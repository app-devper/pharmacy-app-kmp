package app.devper.pharm.presentation.reports.internal

import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.number
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import app.devper.pharm.ui.format.formatYmdDisplay as sharedFormatYmdDisplay
import app.devper.pharm.ui.format.millisToYmd as sharedMillisToYmd
import app.devper.pharm.ui.format.ymdToMillis as sharedYmdToMillis

internal fun millisToYmd(millis: Long?, tz: TimeZone): String = sharedMillisToYmd(millis, tz)

internal fun ymdToMillis(ymd: String, tz: TimeZone): Long? = sharedYmdToMillis(ymd, tz)

internal fun formatYmdDisplay(millis: Long, tz: TimeZone): String = sharedFormatYmdDisplay(millis, tz)

internal fun LocalDate.startOfMonth(): LocalDate = LocalDate(year, month, 1)

internal fun LocalDate.toYmd(): String = buildString {
    append(year)
    append('-')
    append(month.number.toString().padStart(2, '0'))
    append('-')
    append(day.toString().padStart(2, '0'))
}

@OptIn(ExperimentalTime::class)
internal fun todayDate(tz: TimeZone): LocalDate =
    Clock.System.now().toLocalDateTime(tz).date

@OptIn(ExperimentalTime::class)
internal fun LocalDate.toStartOfDayMillis(tz: TimeZone): Long =
    LocalDateTime(this, LocalTime(0, 0))
        .toInstant(tz)
        .toEpochMilliseconds()

enum class ProfitQuickPeriod(val label: String) {
    Today("วันนี้"),
    ThisWeek("สัปดาห์นี้"),
    ThisMonth("เดือนนี้"),
    LastMonth("เดือนที่แล้ว"),
}

internal data class ProfitDateRange(val fromMillis: Long, val toMillis: Long)

internal fun ProfitQuickPeriod.resolve(tz: TimeZone): ProfitDateRange {
    val today = todayDate(tz)
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
