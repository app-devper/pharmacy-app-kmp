package app.devper.pharm.presentation.imports.exception

import app.devper.pharm.common.AppException

sealed class ImportsUiStateError(message: String, cause: Throwable? = null) : AppException(message, cause) {
    class ConfirmFailed(cause: Throwable? = null) : ImportsUiStateError("imports.confirm_failed", cause)
}

sealed class ImportFormUiStateError(message: String, cause: Throwable? = null) : AppException(message, cause) {
    class LoadDrugsFailed(cause: Throwable? = null) : ImportFormUiStateError("imports.form_load_drugs_failed", cause)
    class LoadSuppliersFailed(cause: Throwable? = null) : ImportFormUiStateError("imports.form_load_suppliers_failed", cause)
}
