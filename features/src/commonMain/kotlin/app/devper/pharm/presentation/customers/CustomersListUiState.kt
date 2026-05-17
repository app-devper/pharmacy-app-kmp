package app.devper.pharm.presentation.customers

import app.devper.pharm.domain.model.Customer
import app.devper.pharm.ui.common.BaseUiState

data class CustomersListUiState(
    override val loading: Boolean = false,
    val query: String = "",
    val customers: List<Customer> = emptyList(),
    override val error: String? = null,
) : BaseUiState {
    val filtered: List<Customer>
        get() {
            if (query.isBlank()) return customers
            val q = query.trim().lowercase()
            return customers.filter { c ->
                c.name.lowercase().contains(q) ||
                    (c.phone?.lowercase()?.contains(q) == true)
            }
        }
}
