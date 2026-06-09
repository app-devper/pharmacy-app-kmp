package app.devper.pharm.presentation.suppliers.exception

import app.devper.pharm.common.AppException

sealed class SupplierFormUiStateError(message: String, cause: Throwable? = null) : AppException(message, cause) {
    class NotFound : SupplierFormUiStateError("suppliers.form_not_found")
}
