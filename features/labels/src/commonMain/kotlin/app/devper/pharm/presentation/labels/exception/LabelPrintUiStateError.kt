package app.devper.pharm.presentation.labels.exception

import app.devper.pharm.common.AppException

sealed class LabelPrintUiStateError(message: String, cause: Throwable? = null) : AppException(message, cause) {
    class PrintFailed(cause: Throwable? = null) : LabelPrintUiStateError("labels.print_failed", cause)
}
