package app.devper.pharm.presentation.customers.exception

import app.devper.pharm.common.AppException

sealed class CustomerDetailUiStateError(message: String, cause: Throwable? = null) : AppException(message, cause) {
    class CustomerNotFound : CustomerDetailUiStateError("customers.detail_not_found")
    class LoadCustomerFailed(cause: Throwable? = null) : CustomerDetailUiStateError("customers.detail_load_customer_failed", cause)
    class LoadSalesFailed(cause: Throwable? = null) : CustomerDetailUiStateError("customers.detail_load_sales_failed", cause)
}
