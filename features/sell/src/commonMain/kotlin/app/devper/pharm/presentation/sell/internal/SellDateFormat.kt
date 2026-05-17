package app.devper.pharm.presentation.sell.internal

import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
internal fun todayYmd(tzId: String): String {
    val zone = runCatching { TimeZone.of(tzId) }.getOrDefault(TimeZone.of("Asia/Bangkok"))
    val date: LocalDate = Clock.System.now().toLocalDateTime(zone).date
    @Suppress("DEPRECATION")
    val mm = date.monthNumber.toString().padStart(2, '0')
    @Suppress("DEPRECATION")
    val dd = date.dayOfMonth.toString().padStart(2, '0')
    return "${date.year}-$mm-$dd"
}
