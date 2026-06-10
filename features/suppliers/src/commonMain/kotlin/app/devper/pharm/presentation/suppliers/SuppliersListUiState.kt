package app.devper.pharm.presentation.suppliers

import app.devper.pharm.common.AppException
import app.devper.pharm.domain.model.Supplier
import app.devper.pharm.ui.common.LoadableUiState

data class SuppliersListUiState(
    override val loading: Boolean = false,
    val query: String = "",
    val suppliers: List<Supplier> = emptyList(),
    val pendingDelete: Supplier? = null,
    val deleting: Boolean = false,
    val errorState: AppException? = null,
) : LoadableUiState<SuppliersListUiState> {

    override val domainError: AppException? get() = errorState
    override fun withLoading(value: Boolean) = copy(loading = value)
    override fun withDomainError(error: AppException?) = copy(errorState = error)

    val filtered: List<Supplier> = if (query.isBlank()) {
        suppliers
    } else {
        val q = query.trim().lowercase()
        suppliers.filter { s ->
            s.name.lowercase().contains(q) ||
                s.phone.lowercase().contains(q) ||
                s.contactName.lowercase().contains(q)
        }
    }
}
