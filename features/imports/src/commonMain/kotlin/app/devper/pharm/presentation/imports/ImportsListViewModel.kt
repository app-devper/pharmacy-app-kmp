package app.devper.pharm.presentation.imports

import app.devper.pharm.common.error.CommonUiStateError
import app.devper.pharm.domain.model.PurchaseOrderStatus
import app.devper.pharm.domain.model.PurchaseOrderSummary
import app.devper.pharm.domain.usecase.ConfirmPurchaseOrderUseCase
import app.devper.pharm.domain.usecase.DeletePurchaseOrderUseCase
import app.devper.pharm.domain.usecase.GetPurchaseOrdersUseCase
import app.devper.pharm.presentation.imports.exception.ImportsUiStateError
import app.devper.pharm.ui.common.BaseLoadableViewModel

class ImportsListViewModel(
    private val getPurchaseOrders: GetPurchaseOrdersUseCase,
    private val confirmPurchaseOrder: ConfirmPurchaseOrderUseCase,
    private val deletePurchaseOrder: DeletePurchaseOrderUseCase,
) : BaseLoadableViewModel<ImportsListUiState>(ImportsListUiState()) {

    init { reload() }

    fun onQueryChange(value: String) = setState { copy(query = value) }

    fun reload() {
        setState { copy(loading = true, errorState = null) }
        launchResult(
            block = { getPurchaseOrders() },
            onSuccess = { list -> setState { copy(loading = false, orders = list) } },
            onFailure = { e -> setState { copy(loading = false, errorState = CommonUiStateError.LoadFailed(e)) } },
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
            block = { confirmPurchaseOrder(target.id) },
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
            block = { deletePurchaseOrder(target.id) },
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
