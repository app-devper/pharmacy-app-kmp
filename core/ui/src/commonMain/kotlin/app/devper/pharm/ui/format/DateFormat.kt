package app.devper.pharm.ui.format

import kotlin.time.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime

private val DEFAULT_ZONE = TimeZone.of("Asia/Bangkok")

@OptIn(kotlin.time.ExperimentalTime::class)
fun millisToYmd(millis: Long?, tz: TimeZone = DEFAULT_ZONE): String {
    if (millis == null) return ""
    val date = Instant.fromEpochMilliseconds(millis).toLocalDateTime(tz).date
    val mm = date.month.number.toString().padStart(2, '0')
    val dd = date.day.toString().padStart(2, '0')
    return "${date.year}-$mm-$dd"
}

@OptIn(kotlin.time.ExperimentalTime::class)
fun ymdToMillis(ymd: String, tz: TimeZone = DEFAULT_ZONE): Long? {
    if (ymd.isBlank()) return null
    val date = runCatching { LocalDate.parse(ymd) }.getOrNull() ?: return null
    val dt = LocalDateTime(date, LocalTime(0, 0))
    return dt.toInstant(tz).toEpochMilliseconds()
}

fun formatYmdDisplay(millis: Long, tz: TimeZone = DEFAULT_ZONE): String = millisToYmd(millis, tz)
