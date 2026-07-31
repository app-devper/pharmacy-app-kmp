package app.devper.pharm.presentation.labels.i18n

import app.devper.pharm.common.AppException
import app.devper.pharm.presentation.labels.exception.LabelPrintUiStateError
import app.devper.pharm.ui.i18n.PharmStrings
import app.devper.pharm.ui.i18n.localizeCommon

fun AppException.localizeLabels(s: PharmStrings): String = when (this) {
    is LabelPrintUiStateError.LoadDrugsFailed -> s.labelsLoadDrugsFailed
    is LabelPrintUiStateError.PrintFailed -> s.labelsPrintFailed
    else -> localizeCommon(s)
}
