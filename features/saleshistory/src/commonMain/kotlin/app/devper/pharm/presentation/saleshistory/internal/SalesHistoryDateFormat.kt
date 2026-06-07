package app.devper.pharm.presentation.saleshistory.internal

import kotlinx.datetime.TimeZone
import app.devper.pharm.ui.format.millisToBuddhistDisplay
import app.devper.pharm.ui.format.millisToYmd as sharedMillisToYmd
import app.devper.pharm.ui.format.ymdToMillis as sharedYmdToMillis

internal fun millisToYmd(millis: Long?, tz: TimeZone): String = sharedMillisToYmd(millis, tz)

internal fun ymdToMillis(ymd: String, tz: TimeZone): Long? = sharedYmdToMillis(ymd, tz)

internal fun formatYmdDisplay(millis: Long, tz: TimeZone): String = millisToBuddhistDisplay(millis, tz)
