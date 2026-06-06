package app.devper.pharm.presentation.customers

import app.devper.pharm.domain.model.Customer
import app.devper.pharm.ui.common.BaseUiState

data class CustomersListUiState(
    override val loading: Boolean = false,
    val query: String = "",
    val customers: List<Customer> = emptyList(),
    override val error: String? = null,
) : BaseUiState {
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
