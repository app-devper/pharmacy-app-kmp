package app.devper.pharm.presentation.suppliers.exception

import app.devper.pharm.common.AppException

sealed class SuppliersListUiStateError(message: String, cause: Throwable? = null) : AppException(message, cause) {
    class LoadSuppliersFailed(cause: Throwable? = null) :
        SuppliersListUiStateError("suppliers.list_load_failed", cause)
}
