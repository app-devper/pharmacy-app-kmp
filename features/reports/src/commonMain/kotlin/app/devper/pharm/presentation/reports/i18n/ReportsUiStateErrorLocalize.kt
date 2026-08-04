package app.devper.pharm.presentation.reports.i18n

import app.devper.pharm.common.AppException
import app.devper.pharm.presentation.reports.exception.EodUiStateError
import app.devper.pharm.presentation.reports.exception.ProfitUiStateError
import app.devper.pharm.presentation.reports.exception.ReportsUiStateError
import app.devper.pharm.ui.i18n.PharmStrings
import app.devper.pharm.ui.i18n.localizeCommon

fun AppException.localizeReports(s: PharmStrings): String = when (this) {
    is ReportsUiStateError.LoadSummaryFailed -> s.reportsLoadSummaryFailed
    is ProfitUiStateError.LoadReportFailed -> s.reportsLoadReportFailed
    is EodUiStateError.LoadReportFailed -> s.reportsLoadReportFailed
    is EodUiStateError.CloseFailed -> s.reportsEodCloseFailed
    is EodUiStateError.PrintReceiptUnsupported -> s.reportsEodPrintUnsupported
    else -> localizeCommon(s)
}
