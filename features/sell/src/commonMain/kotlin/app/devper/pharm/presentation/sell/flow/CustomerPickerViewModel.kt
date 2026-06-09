package app.devper.pharm.presentation.sell.flow

import app.devper.pharm.domain.model.Customer
import app.devper.pharm.domain.usecase.ClearCustomerUseCase
import app.devper.pharm.domain.usecase.GetCustomersUseCase
import app.devper.pharm.domain.usecase.SelectCustomerUseCase
import app.devper.pharm.presentation.sell.exception.CustomerPickerUiStateError
import app.devper.pharm.ui.common.BaseViewModel

class CustomerPickerViewModel(
    private val getCustomers: GetCustomersUseCase,
    private val selectCustomer: SelectCustomerUseCase,
    private val clearCustomer: ClearCustomerUseCase,
) : BaseViewModel<CustomerPickerUiState>(CustomerPickerUiState()) {

    fun open() {
        setState { copy(open = true) }
        if (current.customers.isEmpty()) load()
    }

    fun close() = setState { copy(open = false) }

    fun pick(customer: Customer) {
        selectCustomer(customer)
        setState { copy(open = false) }
    }

    fun clear() = clearCustomer()

    fun dismissError() = setState { copy(errorState = null) }

    private fun load() {
        setState { copy(loading = true) }
        launchResult(
            block = { getCustomers() },
            onSuccess = { list -> setState { copy(loading = false, customers = list) } },
            onFailure = { e -> setState { copy(loading = false, errorState = CustomerPickerUiStateError.LoadCustomersFailed(e)) } },
        )
    }
}
