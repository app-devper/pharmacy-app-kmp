package app.devper.pharm.domain.validation

import kotlinx.datetime.TimeZone

fun String.isValidTimeZoneId(): Boolean = isBlank() || runCatching { TimeZone.of(trim()) }.isSuccess
