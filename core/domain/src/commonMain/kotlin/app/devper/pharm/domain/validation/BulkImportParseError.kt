package app.devper.pharm.domain.validation

import app.devper.pharm.common.AppException

sealed class BulkImportParseError(message: String) : AppException(message) {
    class EmptyInput : BulkImportParseError("bulkimport.empty_input")
    class NotArrayOrObject : BulkImportParseError("bulkimport.not_array")
    class RowNotObject(val row: Int) : BulkImportParseError("bulkimport.row_not_object")
    class RowMissingName : BulkImportParseError("bulkimport.row_missing_name")
}
