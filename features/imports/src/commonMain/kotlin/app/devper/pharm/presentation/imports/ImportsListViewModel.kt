package app.devper.pharm.presentation.imports

import app.devper.pharm.domain.repository.purchasing.PurchaseOrderRepository
import app.devper.pharm.common.error.CommonUiStateError
import app.devper.pharm.domain.model.PurchaseOrderStatus
import app.devper.pharm.domain.model.PurchaseOrderSummary
import app.devper.pharm.presentation.imports.exception.ImportsUiStateError
import app.devper.pharm.ui.common.BaseLoadableViewModel

class ImportsListViewModel(
    private val purchaseOrders: PurchaseOrderRepository,
) : BaseLoadableViewModel<ImportsListUiState>(ImportsListUiState()) {

    init { reload() }

    fun onQueryChange(value: String) = setState { copy(query = value) }

    fun reload() {
        load(
            fetch = { Result.success(purchaseOrders.list()) },
            failure = { e -> ImportsUiStateError.LoadOrdersFailed(e) },
            onLoaded = { list -> copy(orders = list) },
        )
    }

    fun requestConfirm(order: PurchaseOrderSummary) {
        if (order.status != PurchaseOrderStatus.Draft) return
        setState { copy(pendingConfirm = order) }
    }
    fun cancelConfirm() = setState { copy(pendingConfirm = null) }

    fun confirmConfirmed() {
        val target = current.pendingConfirm ?: return
        setState { copy(busy = true, errorState = null) }
        launchResult(
            block = { Result.success(purchaseOrders.confirm(target.id)) },
            onSuccess = {
                setState { copy(busy = false, pendingConfirm = null) }
                reload()
            },
            onFailure = { e ->
                setState {
                    copy(
                        busy = false,
                        pendingConfirm = null,
                        errorState = ImportsUiStateError.ConfirmFailed(e),
                    )
                }
            },
        )
    }

    fun requestDelete(order: PurchaseOrderSummary) {
        if (order.status != PurchaseOrderStatus.Draft) return
        setState { copy(pendingDelete = order) }
    }
    fun cancelDelete() = setState { copy(pendingDelete = null) }

    fun deleteConfirmed() {
        val target = current.pendingDelete ?: return
        setState { copy(busy = true, errorState = null) }
        launchResult(
            block = { Result.success(purchaseOrders.delete(target.id)) },
            onSuccess = {
                setState {
                    copy(
                        busy = false,
                        pendingDelete = null,
                        orders = orders.filterNot { o -> o.id == target.id },
                    )
                }
            },
            onFailure = { e ->
                setState {
                    copy(
                        busy = false,
                        pendingDelete = null,
                        errorState = CommonUiStateError.DeleteFailed(e),
                    )
                }
            },
        )
    }
}
