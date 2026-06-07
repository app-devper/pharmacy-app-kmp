package app.devper.pharm.presentation.sell.internal

import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
internal fun todayYmd(tz: TimeZone): String {
    val date: LocalDate = Clock.System.now().toLocalDateTime(tz).date
    val mm = date.month.number.toString().padStart(2, '0')
    val dd = date.day.toString().padStart(2, '0')
    return "${date.year}-$mm-$dd"
}
