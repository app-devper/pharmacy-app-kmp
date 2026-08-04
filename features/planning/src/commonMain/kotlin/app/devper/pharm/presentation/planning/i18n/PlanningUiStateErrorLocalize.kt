package app.devper.pharm.presentation.planning.i18n

import app.devper.pharm.common.AppException
import app.devper.pharm.presentation.planning.exception.LowStockUiStateError
import app.devper.pharm.presentation.planning.exception.ReorderSuggestionsUiStateError
import app.devper.pharm.ui.i18n.PharmStrings
import app.devper.pharm.ui.i18n.localizeCommon

fun AppException.localizePlanning(s: PharmStrings): String = when (this) {
    is LowStockUiStateError.LoadFailed -> s.planningLoadLowStockFailed
    is LowStockUiStateError.TrackStockFailed -> s.planningTrackStockFailed
    is ReorderSuggestionsUiStateError.LoadFailed -> s.planningLoadReorderFailed
    else -> localizeCommon(s)
}
