package app.devper.pharm.presentation.ky.i18n

import app.devper.pharm.common.AppException
import app.devper.pharm.presentation.ky.exception.KyUiStateError
import app.devper.pharm.ui.i18n.PharmStrings
import app.devper.pharm.ui.i18n.localizeCommon

fun AppException.localizeKy(s: PharmStrings): String = when (this) {
    is KyUiStateError.LoadEntriesFailed -> s.kyLoadEntriesFailed
    is KyUiStateError.DownloadPdfFailed -> s.kyDownloadPdfFailed
    else -> localizeCommon(s)
}
