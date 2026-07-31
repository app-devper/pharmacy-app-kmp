package app.devper.pharm.presentation.expiry.i18n

import app.devper.pharm.common.AppException
import app.devper.pharm.presentation.expiry.exception.ExpiryUiStateError
import app.devper.pharm.ui.i18n.PharmStrings
import app.devper.pharm.ui.i18n.localizeCommon

fun AppException.localizeExpiry(s: PharmStrings): String = when (this) {
    is ExpiryUiStateError.LoadLotsFailed -> s.expiryLoadLotsFailed
    is ExpiryUiStateError.WriteoffFailed -> s.expiryWriteoffFailed
    else -> localizeCommon(s)
}
