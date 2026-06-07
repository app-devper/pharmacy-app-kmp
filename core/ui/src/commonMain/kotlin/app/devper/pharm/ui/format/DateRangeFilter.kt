package app.devper.pharm.ui.format

import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone

private val FALLBACK_ZONE = TimeZone.of("Asia/Bangkok")

data class DateRangeFilter(
    val from: String = "",
    val to: String = "",
    val tz: TimeZone = FALLBACK_ZONE,
) {
    val fromDate: LocalDate? get() = from.toLocalDateOrNull()
    val toDate: LocalDate? get() = to.toLocalDateOrNull()
    val fromMillis: Long? get() = ymdToMillis(from, tz)
    val toMillis: Long? get() = ymdToMillis(to, tz)

    fun withFrom(value: String): DateRangeFilter = copy(from = value)
    fun withTo(value: String): DateRangeFilter = copy(to = value)
    fun withFromMillis(millis: Long?): DateRangeFilter = copy(from = millisToYmd(millis, tz))
    fun withToMillis(millis: Long?): DateRangeFilter = copy(to = millisToYmd(millis, tz))
}
