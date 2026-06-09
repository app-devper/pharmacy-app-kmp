package app.devper.pharm.presentation.customers

import app.devper.pharm.common.error.CommonUiStateError
import app.devper.pharm.domain.usecase.GetCustomersUseCase
import app.devper.pharm.ui.common.BaseLoadableViewModel

class CustomersListViewModel(
    private val getCustomers: GetCustomersUseCase,
) : BaseLoadableViewModel<CustomersListUiState>(CustomersListUiState()) {

    init { reload() }

    fun onQueryChange(value: String) = setState { copy(query = value) }

    fun reload() {
        setState { copy(loading = true, errorState = null) }
        launchResult(
            block = { getCustomers() },
            onSuccess = { list -> setState { copy(loading = false, customers = list) } },
            onFailure = { e -> setState { copy(loading = false, errorState = CommonUiStateError.LoadFailed(e)) } },
        )
    }
}
