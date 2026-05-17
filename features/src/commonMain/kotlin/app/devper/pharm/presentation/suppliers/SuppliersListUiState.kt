package app.devper.pharm.presentation.suppliers

import app.devper.pharm.domain.model.Supplier
import app.devper.pharm.ui.common.BaseUiState

data class SuppliersListUiState(
    override val loading: Boolean = false,
    val query: String = "",
    val suppliers: List<Supplier> = emptyList(),
    val pendingDelete: Supplier? = null,
    val deleting: Boolean = false,
    override val error: String? = null,
) : BaseUiState {
    val filtered: List<Supplier>
        get() {
            if (query.isBlank()) return suppliers
            val q = query.trim().lowercase()
            return suppliers.filter { s ->
                s.name.lowercase().contains(q) ||
                    s.phone.lowercase().contains(q) ||
                    s.contactName.lowercase().contains(q)
            }
        }
}
