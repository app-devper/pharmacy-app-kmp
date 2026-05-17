package app.devper.pharm.presentation.imports

import app.devper.pharm.domain.model.PurchaseOrderStatus
import app.devper.pharm.domain.model.PurchaseOrderSummary
import app.devper.pharm.ui.common.BaseUiState

data class ImportsListUiState(
    override val loading: Boolean = false,
    val query: String = "",
    val orders: List<PurchaseOrderSummary> = emptyList(),
    val pendingConfirm: PurchaseOrderSummary? = null,
    val pendingDelete: PurchaseOrderSummary? = null,
    val busy: Boolean = false,
    override val error: String? = null,
) : BaseUiState {
    val filtered: List<PurchaseOrderSummary>
        get() {
            if (query.isBlank()) return orders
            val q = query.trim().lowercase()
            return orders.filter { o ->
                o.docNo.lowercase().contains(q) ||
                    o.supplier.lowercase().contains(q) ||
                    o.invoiceNo.lowercase().contains(q)
            }
        }

    val draftCount: Int
        get() = orders.count { it.status == PurchaseOrderStatus.Draft }
}
