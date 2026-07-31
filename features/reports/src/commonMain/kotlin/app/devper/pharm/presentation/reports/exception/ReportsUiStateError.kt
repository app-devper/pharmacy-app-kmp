package app.devper.pharm.presentation.reports.exception

import app.devper.pharm.common.AppException

sealed class ReportsUiStateError(message: String, cause: Throwable? = null) : AppException(message, cause) {
    class LoadSummaryFailed(cause: Throwable? = null) : ReportsUiStateError("reports.load_summary_failed", cause)
}

sealed class ProfitUiStateError(message: String, cause: Throwable? = null) : AppException(message, cause) {
    class LoadReportFailed(cause: Throwable? = null) : ProfitUiStateError("reports.load_report_failed", cause)
}

sealed class EodUiStateError(message: String, cause: Throwable? = null) : AppException(message, cause) {
    class LoadReportFailed(cause: Throwable? = null) : EodUiStateError("reports.eod_load_failed", cause)
    class CloseFailed(cause: Throwable? = null) : EodUiStateError("reports.eod_close_failed", cause)
    class PrintReceiptUnsupported(cause: Throwable? = null) : EodUiStateError("reports.eod_print_unsupported", cause)
}
