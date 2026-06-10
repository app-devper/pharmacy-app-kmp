package app.devper.pharm.presentation.imports

import app.devper.pharm.domain.model.PurchaseOrderStatus
import app.devper.pharm.common.AppException
import app.devper.pharm.domain.model.PurchaseOrderSummary
import app.devper.pharm.ui.common.LoadableUiState

data class ImportsListUiState(
    override val loading: Boolean = false,
    val query: String = "",
    val orders: List<PurchaseOrderSummary> = emptyList(),
    val pendingConfirm: PurchaseOrderSummary? = null,
    val pendingDelete: PurchaseOrderSummary? = null,
    val busy: Boolean = false,
    val errorState: AppException? = null,
) : LoadableUiState<ImportsListUiState> {

    override val domainError: AppException? get() = errorState

    override fun withLoading(value: Boolean) = copy(loading = value)
    override fun withDomainError(error: AppException?) = copy(errorState = error)

    val filtered: List<PurchaseOrderSummary> = if (query.isBlank()) {
        orders
    } else {
        val q = query.trim().lowercase()
        orders.filter { o ->
            o.docNo.lowercase().contains(q) ||
                o.supplier.lowercase().contains(q) ||
                o.invoiceNo.lowercase().contains(q)
        }
    }

    val draftCount: Int = orders.count { it.status == PurchaseOrderStatus.Draft }
}
