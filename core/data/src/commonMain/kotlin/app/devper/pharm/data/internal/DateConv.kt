package app.devper.pharm.data.internal

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime

fun String?.parseLocalDateOrNull(): LocalDate? {
    if (this.isNullOrBlank()) return null
    return runCatching { LocalDate.parse(this) }.getOrNull()
}

fun String.parseLocalDateOrEpoch(): LocalDate =
    parseLocalDateOrNull() ?: LocalDate(1970, 1, 1)

fun String?.parseLocalDateTimeOrNull(): LocalDateTime? {
    if (this.isNullOrBlank()) return null
    val trimmed = this.removeSuffix("Z").substringBefore('+').take(19)
    return runCatching { LocalDateTime.parse(trimmed) }.getOrNull()
}

fun String.parseLocalDateTimeOrEpoch(): LocalDateTime =
    parseLocalDateTimeOrNull() ?: LocalDateTime(1970, 1, 1, 0, 0)

fun LocalDate.toIso(): String = toString()

fun LocalDateTime.toIso(): String = toString()
