package app.devper.pharm.domain.observer

import kotlinx.datetime.TimeZone

private val FALLBACK = TimeZone.of("Asia/Bangkok")

class TimeZoneProvider(private val source: () -> String) {
    val current: TimeZone
        get() = runCatching { TimeZone.of(source()) }.getOrDefault(FALLBACK)
}
