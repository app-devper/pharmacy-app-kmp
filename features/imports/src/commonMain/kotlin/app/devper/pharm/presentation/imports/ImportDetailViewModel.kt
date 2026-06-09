package app.devper.pharm.presentation.imports

import app.devper.pharm.common.error.CommonUiStateError
import app.devper.pharm.domain.usecase.ConfirmPurchaseOrderUseCase
import app.devper.pharm.domain.usecase.DeletePurchaseOrderUseCase
import app.devper.pharm.domain.usecase.GetPurchaseOrderUseCase
import app.devper.pharm.presentation.imports.exception.ImportsUiStateError
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
        setState { copy(loading = true, errorState = null) }
        launchResult(
            block = { getPurchaseOrder(id) },
            onSuccess = { po -> setState { copy(loading = false, po = po) } },
            onFailure = { e -> setState { copy(loading = false, errorState = CommonUiStateError.LoadFailed(e)) } },
        )
    }

    fun askConfirm() = setState { copy(confirmDialog = true) }
    fun cancelConfirm() = setState { copy(confirmDialog = false) }
    fun askDelete() = setState { copy(deleteDialog = true) }
    fun cancelDelete() = setState { copy(deleteDialog = false) }
    fun dismissError() = setState { copy(errorState = null) }

    fun confirmNow() {
        val id = importId ?: return
        setState { copy(confirmDialog = false, confirming = true, errorState = null) }
        launchResult(
            block = { confirmPurchaseOrder(id) },
            onSuccess = { po -> setState { copy(confirming = false, po = po) } },
            onFailure = { e -> setState { copy(confirming = false, errorState = ImportsUiStateError.ConfirmFailed(e)) } },
        )
    }

    fun deleteNow() {
        val id = importId ?: return
        setState { copy(deleteDialog = false, deleting = true, errorState = null) }
        launchResult(
            block = { deletePurchaseOrder(id) },
            onSuccess = { setState { copy(deleting = false, closed = true) } },
            onFailure = { e -> setState { copy(deleting = false, errorState = CommonUiStateError.DeleteFailed(e)) } },
        )
    }
}
