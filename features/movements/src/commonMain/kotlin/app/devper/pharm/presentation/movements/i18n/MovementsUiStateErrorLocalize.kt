package app.devper.pharm.presentation.movements.i18n

import app.devper.pharm.common.AppException
import app.devper.pharm.presentation.movements.exception.MovementsUiStateError
import app.devper.pharm.ui.i18n.PharmStrings
import app.devper.pharm.ui.i18n.localizeCommon

fun AppException.localizeMovements(s: PharmStrings): String = when (this) {
    is MovementsUiStateError.LoadHistoryFailed -> s.movementsLoadHistoryFailed
    else -> localizeCommon(s)
}
