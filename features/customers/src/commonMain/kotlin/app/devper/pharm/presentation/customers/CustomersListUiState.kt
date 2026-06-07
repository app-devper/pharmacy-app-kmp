package app.devper.pharm.presentation.customers

import app.devper.pharm.domain.model.Customer
import app.devper.pharm.ui.common.LoadableUiState

data class CustomersListUiState(
    override val loading: Boolean = false,
    val query: String = "",
    val customers: List<Customer> = emptyList(),
    override val error: String? = null,
) : LoadableUiState<CustomersListUiState> {

    override fun withLoading(value: Boolean) = copy(loading = value)
    override fun withError(value: String?) = copy(error = value)

    val filtered: List<Customer> = if (query.isBlank()) {
        customers
    } else {
        val q = query.trim().lowercase()
        customers.filter { c ->
            c.name.lowercase().contains(q) ||
                (c.phone?.lowercase()?.contains(q) == true)
        }
    }
}
