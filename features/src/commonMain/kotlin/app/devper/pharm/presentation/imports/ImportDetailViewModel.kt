package app.devper.pharm.presentation.imports

import app.devper.pharm.domain.usecase.ConfirmPurchaseOrderUseCase
import app.devper.pharm.domain.usecase.DeletePurchaseOrderUseCase
import app.devper.pharm.domain.usecase.GetPurchaseOrderUseCase
import app.devper.pharm.ui.common.BaseViewModel

class ImportDetailViewModel(
    private val getPurchaseOrder: GetPurchaseOrderUseCase,
    private val confirmPurchaseOrder: ConfirmPurchaseOrderUseCase,
    private val deletePurchaseOrder: DeletePurchaseOrderUseCase,
) : BaseViewModel<ImportDetailUiState>(ImportDetailUiState()) {

    private var importId: String? = null

    fun init(id: String) {
        importId = id
        reload()
    }

    fun reload() {
        val id = importId ?: return
        setState { copy(loading = true, error = null) }
        launchResult(
            block = { getPurchaseOrder(id) },
            onSuccess = { po -> setState { copy(loading = false, po = po) } },
            onFailure = { e -> setState { copy(loading = false, error = e.message ?: "โหลดข้อมูลไม่สำเร็จ") } },
        )
    }

    fun askConfirm() = setState { copy(confirmDialog = true) }
    fun cancelConfirm() = setState { copy(confirmDialog = false) }
    fun askDelete() = setState { copy(deleteDialog = true) }
    fun cancelDelete() = setState { copy(deleteDialog = false) }
    fun dismissError() = setState { copy(error = null) }

    fun confirmNow() {
        val id = importId ?: return
        setState { copy(confirmDialog = false, confirming = true, error = null) }
        launchResult(
            block = { confirmPurchaseOrder(id) },
            onSuccess = { po -> setState { copy(confirming = false, po = po) } },
            onFailure = { e -> setState { copy(confirming = false, error = e.message ?: "ยืนยันไม่สำเร็จ") } },
        )
    }

    fun deleteNow() {
        val id = importId ?: return
        setState { copy(deleteDialog = false, deleting = true, error = null) }
        launchResult(
            block = { deletePurchaseOrder(id) },
            onSuccess = { setState { copy(deleting = false, closed = true) } },
            onFailure = { e -> setState { copy(deleting = false, error = e.message ?: "ลบไม่สำเร็จ") } },
        )
    }
}
