package app.devper.pharm.presentation.customers.exception

import app.devper.pharm.common.AppException

sealed class CustomersListUiStateError(message: String, cause: Throwable? = null) : AppException(message, cause) {
    class LoadCustomersFailed(cause: Throwable? = null) :
        CustomersListUiStateError("customers.list_load_failed", cause)
}
