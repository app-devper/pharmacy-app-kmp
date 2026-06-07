package app.devper.pharm.data.internal

import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

private val BANGKOK = TimeZone.of("Asia/Bangkok")

fun String?.parseLocalDateOrNull(): LocalDate? {
    if (this.isNullOrBlank()) return null
    return runCatching { LocalDate.parse(this) }.getOrNull()
}

fun String.parseLocalDateOrEpoch(): LocalDate =
    parseLocalDateOrNull() ?: LocalDate(1970, 1, 1)

@OptIn(ExperimentalTime::class)
fun String?.parseLocalDateTimeOrNull(): LocalDateTime? {
    if (this.isNullOrBlank()) return null
    val asInstant = runCatching { Instant.parse(this) }.getOrNull()
    if (asInstant != null) return asInstant.toLocalDateTime(BANGKOK)
    val trimmed = this.take(19)
    return runCatching { LocalDateTime.parse(trimmed) }.getOrNull()
}

fun String.parseLocalDateTimeOrEpoch(): LocalDateTime =
    parseLocalDateTimeOrNull() ?: LocalDateTime(1970, 1, 1, 0, 0)

fun LocalDate.toIso(): String = toString()

fun LocalDateTime.toIso(): String = toString()
