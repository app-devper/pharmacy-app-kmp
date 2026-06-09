package app.devper.pharm.common.error

import app.devper.pharm.common.AppException

sealed class CommonUiStateError(message: String, cause: Throwable? = null) : AppException(message, cause) {
    class LoadFailed(cause: Throwable? = null) : CommonUiStateError("common.load_failed", cause)
    class SaveFailed(cause: Throwable? = null) : CommonUiStateError("common.save_failed", cause)
    class DeleteFailed(cause: Throwable? = null) : CommonUiStateError("common.delete_failed", cause)
    class ExportFailed(cause: Throwable? = null) : CommonUiStateError("common.export_failed", cause)
}
