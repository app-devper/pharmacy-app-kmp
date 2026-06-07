package app.devper.pharm.presentation.imports

import app.devper.pharm.common.error.ErrorMessages

import app.devper.pharm.domain.model.PurchaseOrderStatus
import app.devper.pharm.domain.model.PurchaseOrderSummary
import app.devper.pharm.domain.usecase.ConfirmPurchaseOrderUseCase
import app.devper.pharm.domain.usecase.DeletePurchaseOrderUseCase
import app.devper.pharm.domain.usecase.GetPurchaseOrdersUseCase
import app.devper.pharm.ui.common.BaseLoadableViewModel

class ImportsListViewModel(
    private val getPurchaseOrders: GetPurchaseOrdersUseCase,
    private val confirmPurchaseOrder: ConfirmPurchaseOrderUseCase,
    private val deletePurchaseOrder: DeletePurchaseOrderUseCase,
) : BaseLoadableViewModel<ImportsListUiState>(ImportsListUiState()) {

    init { reload() }

    fun onQueryChange(value: String) = setState { copy(query = value) }

    fun reload() = launchLoad(
        block = { getPurchaseOrders() },
        onSuccess = { list -> copy(orders = list) },
    )

    fun requestConfirm(order: PurchaseOrderSummary) {
        if (order.status != PurchaseOrderStatus.Draft) return
        setState { copy(pendingConfirm = order) }
    }
    fun cancelConfirm() = setState { copy(pendingConfirm = null) }

    fun confirmConfirmed() {
        val target = current.pendingConfirm ?: return
        setState { copy(busy = true, error = null) }
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
                        error = e.message ?: "ยืนยันรับเข้าไม่สำเร็จ",
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
        setState { copy(busy = true, error = null) }
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
                        error = e.message ?: ErrorMessages.DELETE_FAILED,
                    )
                }
            },
        )
    }
}
