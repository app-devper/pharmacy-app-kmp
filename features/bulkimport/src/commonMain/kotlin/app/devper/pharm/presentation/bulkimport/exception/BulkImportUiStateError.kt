package app.devper.pharm.presentation.bulkimport.exception

import app.devper.pharm.common.AppException

sealed class BulkImportUiStateError(message: String, cause: Throwable? = null) : AppException(message, cause) {
    class PickFileFailed(cause: Throwable? = null) : BulkImportUiStateError("bulkimport.pick_file_failed", cause)
    class ImportFailed(cause: Throwable? = null) : BulkImportUiStateError("bulkimport.import_failed", cause)
    class NoRows : BulkImportUiStateError("bulkimport.no_rows")
    class InvalidJson(cause: Throwable? = null) : BulkImportUiStateError("bulkimport.invalid_json", cause)
}
