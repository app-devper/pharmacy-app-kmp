package app.devper.pharm.presentation.movements.internal

import kotlin.time.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime

@OptIn(kotlin.time.ExperimentalTime::class)
internal fun millisToYmd(millis: Long?): String {
    if (millis == null) return ""
    val date = Instant.fromEpochMilliseconds(millis).toLocalDateTime(TimeZone.currentSystemDefault()).date
    val mm = date.month.number.toString().padStart(2, '0')
    val dd = date.day.toString().padStart(2, '0')
    return "${date.year}-$mm-$dd"
}

@OptIn(kotlin.time.ExperimentalTime::class)
internal fun ymdToMillis(ymd: String): Long? {
    if (ymd.isBlank()) return null
    val date = runCatching { LocalDate.parse(ymd) }.getOrNull() ?: return null
    val dt = LocalDateTime(date, LocalTime(0, 0))
    return dt.toInstant(TimeZone.currentSystemDefault()).toEpochMilliseconds()
}

internal fun formatYmdDisplay(millis: Long): String = millisToYmd(millis)
