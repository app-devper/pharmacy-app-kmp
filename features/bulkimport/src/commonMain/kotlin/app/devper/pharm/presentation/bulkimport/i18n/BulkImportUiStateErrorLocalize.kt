package app.devper.pharm.presentation.bulkimport.i18n

import app.devper.pharm.common.AppException
import app.devper.pharm.presentation.bulkimport.exception.BulkImportUiStateError
import app.devper.pharm.ui.i18n.PharmStrings
import app.devper.pharm.ui.i18n.localizeCommon

fun AppException.localizeBulkImport(s: PharmStrings): String = when (this) {
    is BulkImportUiStateError.PickFileFailed -> s.bulkImportPickFileFailed
    is BulkImportUiStateError.ImportFailed -> s.bulkImportImportFailed
    is BulkImportUiStateError.NoRows -> s.bulkImportNoRows
    is BulkImportUiStateError.InvalidJson -> s.bulkImportInvalidJson
    else -> localizeCommon(s)
}
