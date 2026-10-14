package app.devper.pharm.presentation.customers

import app.devper.pharm.domain.repository.customers.CustomerRepository
import app.devper.pharm.presentation.customers.exception.CustomersListUiStateError
import app.devper.pharm.ui.common.BaseLoadableViewModel

class CustomersListViewModel(
    private val customers: CustomerRepository,
) : BaseLoadableViewModel<CustomersListUiState>(CustomersListUiState()) {

    init { reload() }

    fun onQueryChange(value: String) = setState { copy(query = value) }

    fun reload() {
        load(
            fetch = { Result.success(customers.list()) },
            failure = { e -> CustomersListUiStateError.LoadCustomersFailed(e) },
            onLoaded = { list -> copy(customers = list) },
        )
    }
}
