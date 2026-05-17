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
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import app.devper.pharm.ui.format.formatYmdDisplay as sharedFormatYmdDisplay
import app.devper.pharm.ui.format.millisToYmd as sharedMillisToYmd
import app.devper.pharm.ui.format.ymdToMillis as sharedYmdToMillis

internal fun millisToYmd(millis: Long?): String = sharedMillisToYmd(millis)

internal fun ymdToMillis(ymd: String): Long? = sharedYmdToMillis(ymd)

internal fun formatYmdDisplay(millis: Long): String = sharedFormatYmdDisplay(millis)

@OptIn(ExperimentalTime::class)
internal fun todayDate(): LocalDate =
    Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date

@OptIn(ExperimentalTime::class)
internal fun LocalDate.toStartOfDayMillis(): Long =
    LocalDateTime(this, LocalTime(0, 0))
        .toInstant(TimeZone.currentSystemDefault())
        .toEpochMilliseconds()

enum class ProfitQuickPeriod(val label: String) {
    Today("วันนี้"),
    ThisWeek("สัปดาห์นี้"),
    ThisMonth("เดือนนี้"),
    LastMonth("เดือนที่แล้ว"),
}

internal data class ProfitDateRange(val fromMillis: Long, val toMillis: Long)

internal fun ProfitQuickPeriod.resolve(): ProfitDateRange {
    val today = todayDate()
    return when (this) {
        ProfitQuickPeriod.Today -> ProfitDateRange(today.toStartOfDayMillis(), today.toStartOfDayMillis())
        ProfitQuickPeriod.ThisWeek -> {
            val daysFromMonday = ((today.dayOfWeek.ordinal - DayOfWeek.MONDAY.ordinal) + 7) % 7
            val start = today.minus(daysFromMonday, DateTimeUnit.DAY)
            ProfitDateRange(start.toStartOfDayMillis(), today.toStartOfDayMillis())
        }
        ProfitQuickPeriod.ThisMonth -> {
            @Suppress("DEPRECATION")
            val start = LocalDate(today.year, today.monthNumber, 1)
            ProfitDateRange(start.toStartOfDayMillis(), today.toStartOfDayMillis())
        }
        ProfitQuickPeriod.LastMonth -> {
            @Suppress("DEPRECATION")
            val firstOfThisMonth = LocalDate(today.year, today.monthNumber, 1)
            val lastOfPrev = firstOfThisMonth.minus(1, DateTimeUnit.DAY)
            @Suppress("DEPRECATION")
            val firstOfPrev = LocalDate(lastOfPrev.year, lastOfPrev.monthNumber, 1)
            ProfitDateRange(firstOfPrev.toStartOfDayMillis(), lastOfPrev.toStartOfDayMillis())
        }
    }
}
