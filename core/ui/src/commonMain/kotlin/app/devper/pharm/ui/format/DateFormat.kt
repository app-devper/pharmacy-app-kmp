package app.devper.pharm.ui.format

import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime

private val DEFAULT_ZONE = TimeZone.of("Asia/Bangkok")
private const val BUDDHIST_ERA_OFFSET = 543

@OptIn(ExperimentalTime::class)
fun millisToYmd(millis: Long?, tz: TimeZone = DEFAULT_ZONE): String {
    if (millis == null) return ""
    val date = Instant.fromEpochMilliseconds(millis).toLocalDateTime(tz).date
    val mm = date.month.number.toString().padStart(2, '0')
    val dd = date.day.toString().padStart(2, '0')
    return "${date.year}-$mm-$dd"
}

@OptIn(ExperimentalTime::class)
fun ymdToMillis(ymd: String, tz: TimeZone = DEFAULT_ZONE): Long? {
    if (ymd.isBlank()) return null
    val date = runCatching { LocalDate.parse(ymd) }.getOrNull() ?: return null
    val dt = LocalDateTime(date, LocalTime(0, 0))
    return dt.toInstant(tz).toEpochMilliseconds()
}

fun formatYmdDisplay(millis: Long, tz: TimeZone = DEFAULT_ZONE): String = millisToYmd(millis, tz)

fun toBuddhistEraDisplay(date: LocalDate): String {
    val dd = date.day.toString().padStart(2, '0')
    val mm = date.month.number.toString().padStart(2, '0')
    val yyyy = date.year + BUDDHIST_ERA_OFFSET
    return "$dd/$mm/$yyyy"
}

@OptIn(ExperimentalTime::class)
fun millisToBuddhistDisplay(millis: Long?, tz: TimeZone = DEFAULT_ZONE): String {
    if (millis == null) return ""
    val date = Instant.fromEpochMilliseconds(millis).toLocalDateTime(tz).date
    return toBuddhistEraDisplay(date)
}

@OptIn(ExperimentalTime::class)
fun millisToBuddhistDisplayWithTime(millis: Long, tz: TimeZone = DEFAULT_ZONE): String {
    val dt = Instant.fromEpochMilliseconds(millis).toLocalDateTime(tz)
    val datePart = toBuddhistEraDisplay(dt.date)
    val hh = dt.hour.toString().padStart(2, '0')
    val mi = dt.minute.toString().padStart(2, '0')
    return "$datePart $hh:$mi"
}

@OptIn(ExperimentalTime::class)
fun todayBuddhistDisplay(tz: TimeZone = DEFAULT_ZONE): String =
    toBuddhistEraDisplay(Clock.System.now().toLocalDateTime(tz).date)

fun isoDateToBuddhist(s: String): String {
    if (s.isBlank()) return ""
    val date = runCatching { LocalDate.parse(s) }.getOrNull() ?: return s
    return toBuddhistEraDisplay(date)
}

fun isoDateTimeToBuddhist(s: String): String {
    if (s.isBlank()) return ""
    val trimmed = s.removeSuffix("Z").substringBefore('+').take(19)
    val dt = runCatching { LocalDateTime.parse(trimmed) }.getOrNull() ?: return s
    return localDateTimeToBuddhist(dt)
}

fun localDateToBuddhist(date: LocalDate?): String =
    if (date == null) "" else toBuddhistEraDisplay(date)

fun localDateTimeToBuddhist(dt: LocalDateTime?): String {
    if (dt == null) return ""
    val datePart = toBuddhistEraDisplay(dt.date)
    val hh = dt.hour.toString().padStart(2, '0')
    val mi = dt.minute.toString().padStart(2, '0')
    return "$datePart $hh:$mi"
}

fun LocalDate.toIsoYmd(): String = toString()

fun String.toLocalDateOrNull(): LocalDate? {
    if (isBlank()) return null
    return runCatching { LocalDate.parse(this) }.getOrNull()
}

fun LocalDate.formatBuddhist(): String = toBuddhistEraDisplay(this)

fun LocalDateTime.formatBuddhist(): String = localDateTimeToBuddhist(this)
