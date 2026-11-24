package app.devper.pharm.presentation.customers

import app.devper.pharm.domain.repository.customers.CustomerRepository
import app.devper.pharm.presentation.customers.exception.CustomerDetailUiStateError
import app.devper.pharm.ui.common.BaseViewModel

class CustomerDetailViewModel(
    private val customers: CustomerRepository,
) : BaseViewModel<CustomerDetailUiState>(CustomerDetailUiState()) {

    fun load(customerId: String) {
        setState {
            copy(customer = null, sales = emptyList(), customerLoading = true, salesLoading = true)
        }
        launchCatching(
            block = { customers.list() },
            onSuccess = { list ->
                val c = list.firstOrNull { it.id == customerId }
                setState { copy(customerLoading = false, customer = c, errorState = errorState ?: if (c == null) CustomerDetailUiStateError.CustomerNotFound() else null) }
            },
            onFailure = { e ->
                setState { copy(customerLoading = false, errorState = errorState ?: CustomerDetailUiStateError.LoadCustomerFailed(e)) }
            },
        )
        launchCatching(
            block = { customers.getCustomerSales(customerId) },
            onSuccess = { list -> setState { copy(salesLoading = false, sales = list) } },
            onFailure = { e ->
                setState { copy(salesLoading = false, errorState = errorState ?: CustomerDetailUiStateError.LoadSalesFailed(e)) }
            },
        )
    }

    fun dismissError() = setState { copy(errorState = null) }
}
