package app.devper.pharm.domain.observer

import kotlinx.datetime.TimeZone

fun testTimeZoneProvider(tz: TimeZone = TimeZone.of("Asia/Bangkok")): TimeZoneProvider =
    TimeZoneProvider { tz.id }
