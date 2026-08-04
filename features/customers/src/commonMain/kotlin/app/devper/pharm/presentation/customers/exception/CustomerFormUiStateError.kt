package app.devper.pharm.presentation.customers.exception

import app.devper.pharm.common.AppException

sealed class CustomerFormUiStateError(message: String, cause: Throwable? = null) : AppException(message, cause) {
    class LoadCustomerFailed(cause: Throwable? = null) : CustomerFormUiStateError("customers.form_load_failed", cause)
    class NotFound : CustomerFormUiStateError("customers.form_not_found")
}
