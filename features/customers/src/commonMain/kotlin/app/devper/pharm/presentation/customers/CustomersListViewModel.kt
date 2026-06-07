package app.devper.pharm.presentation.customers

import app.devper.pharm.domain.usecase.GetCustomersUseCase
import app.devper.pharm.ui.common.BaseLoadableViewModel

class CustomersListViewModel(
    private val getCustomers: GetCustomersUseCase,
) : BaseLoadableViewModel<CustomersListUiState>(CustomersListUiState()) {

    init { reload() }

    fun onQueryChange(value: String) = setState { copy(query = value) }

    fun reload() = launchLoad(
        block = { getCustomers() },
        onSuccess = { list -> copy(customers = list) },
    )
}
