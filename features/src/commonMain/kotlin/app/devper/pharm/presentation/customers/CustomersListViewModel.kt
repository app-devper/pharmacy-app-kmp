package app.devper.pharm.presentation.customers

import app.devper.pharm.domain.usecase.GetCustomersUseCase
import app.devper.pharm.ui.common.BaseViewModel

class CustomersListViewModel(
    private val getCustomers: GetCustomersUseCase,
) : BaseViewModel<CustomersListUiState>(CustomersListUiState()) {

    init { reload() }

    fun onQueryChange(value: String) = setState { copy(query = value) }
    fun dismissError() = setState { copy(error = null) }

    fun reload() {
        setState { copy(loading = true, error = null) }
        launchResult(
            block = { getCustomers() },
            onSuccess = { list -> setState { copy(loading = false, customers = list) } },
            onFailure = { e ->
                setState { copy(loading = false, error = e.message ?: "โหลดข้อมูลไม่สำเร็จ") }
            },
        )
    }
}
