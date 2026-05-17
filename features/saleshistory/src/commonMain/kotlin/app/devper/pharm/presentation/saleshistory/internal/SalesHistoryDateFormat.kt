package app.devper.pharm.presentation.saleshistory.internal

import app.devper.pharm.ui.format.formatYmdDisplay as sharedFormatYmdDisplay
import app.devper.pharm.ui.format.millisToYmd as sharedMillisToYmd
import app.devper.pharm.ui.format.ymdToMillis as sharedYmdToMillis

internal fun millisToYmd(millis: Long?): String = sharedMillisToYmd(millis)

internal fun ymdToMillis(ymd: String): Long? = sharedYmdToMillis(ymd)

internal fun formatYmdDisplay(millis: Long): String = sharedFormatYmdDisplay(millis)
