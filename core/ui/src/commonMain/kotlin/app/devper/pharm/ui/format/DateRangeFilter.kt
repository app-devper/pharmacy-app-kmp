package app.devper.pharm.ui.format

import app.devper.pharm.ui.designsystem.PharmDateQuickPeriod
import app.devper.pharm.ui.designsystem.PharmDateRange
import app.devper.pharm.ui.i18n.PharmStrings
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus

private val FALLBACK_ZONE = TimeZone.of("Asia/Bangkok")

enum class QuickPeriod { Today, Last7Days, ThisWeek, ThisMonth, LastMonth }

fun QuickPeriod.dates(today: LocalDate): Pair<LocalDate, LocalDate> = when (this) {
    QuickPeriod.Today -> today to today
    QuickPeriod.Last7Days -> today.minus(DatePeriod(days = 6)) to today
    QuickPeriod.ThisWeek -> today.minus(DatePeriod(days = (today.dayOfWeek.ordinal - DayOfWeek.MONDAY.ordinal + 7) % 7)) to today
    QuickPeriod.ThisMonth -> today.startOfMonth() to today
    QuickPeriod.LastMonth -> {
        val lastOfPrevious = today.startOfMonth().minus(DatePeriod(days = 1))
        lastOfPrevious.startOfMonth() to lastOfPrevious
    }
}

fun QuickPeriod.label(s: PharmStrings): String = when (this) {
    QuickPeriod.Today -> s.reportsRangeToday
    QuickPeriod.Last7Days -> s.salesHistoryRange7d
    QuickPeriod.ThisWeek -> s.reportsRangeThisWeek
    QuickPeriod.ThisMonth -> s.reportsRangeThisMonth
    QuickPeriod.LastMonth -> s.reportsRangeLastMonth
}

data class DateRangeFilter(
    val from: String = "",
    val to: String = "",
    val tz: TimeZone = FALLBACK_ZONE,
) {
    val fromDate: LocalDate? get() = from.toLocalDateOrNull()
    val toDate: LocalDate? get() = to.toLocalDateOrNull()
    val fromMillis: Long? get() = ymdToMillis(from)
    val toMillis: Long? get() = ymdToMillis(to)
    val range: PharmDateRange get() = PharmDateRange(fromMillis = fromMillis, toMillis = toMillis)

    fun withFrom(value: String): DateRangeFilter = copy(from = value)
    fun withTo(value: String): DateRangeFilter = copy(to = value)
    fun withRange(range: PharmDateRange): DateRangeFilter =
        copy(from = millisToYmd(range.fromMillis), to = millisToYmd(range.toMillis))

    fun withPeriod(period: QuickPeriod, today: LocalDate = todayLocalDate(tz)): DateRangeFilter {
        val (start, end) = period.dates(today)
        return copy(from = start.toYmd(), to = end.toYmd())
    }

    fun activePeriod(offered: List<QuickPeriod>, today: LocalDate = todayLocalDate(tz)): QuickPeriod? =
        offered.firstOrNull { withPeriod(it, today) == this }

    fun quickPeriods(offered: List<QuickPeriod>, s: PharmStrings, today: LocalDate = todayLocalDate(tz)): List<PharmDateQuickPeriod> =
        offered.map { period ->
            val chosen = withPeriod(period, today)
            PharmDateQuickPeriod(label = period.label(s), fromMillis = chosen.fromMillis ?: 0L, toMillis = chosen.toMillis ?: 0L)
        }
}
