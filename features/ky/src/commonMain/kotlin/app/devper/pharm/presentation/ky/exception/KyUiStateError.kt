package app.devper.pharm.presentation.ky.exception

import app.devper.pharm.common.AppException

sealed class KyUiStateError(message: String, cause: Throwable? = null) : AppException(message, cause) {
    class DownloadPdfFailed(cause: Throwable? = null) : KyUiStateError("ky.download_pdf_failed", cause)
}
