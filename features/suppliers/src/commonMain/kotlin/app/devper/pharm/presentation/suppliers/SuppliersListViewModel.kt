package app.devper.pharm.presentation.suppliers

import app.devper.pharm.common.error.ErrorMessages

import app.devper.pharm.domain.model.Supplier
import app.devper.pharm.domain.usecase.DeleteSupplierUseCase
import app.devper.pharm.domain.usecase.GetSuppliersUseCase
import app.devper.pharm.ui.common.BaseLoadableViewModel

class SuppliersListViewModel(
    private val getSuppliers: GetSuppliersUseCase,
    private val deleteSupplier: DeleteSupplierUseCase,
) : BaseLoadableViewModel<SuppliersListUiState>(SuppliersListUiState()) {

    init { reload() }

    fun onQueryChange(value: String) = setState { copy(query = value) }

    fun reload() = launchLoad(
        block = { getSuppliers() },
        onSuccess = { list -> copy(suppliers = list) },
    )

    fun confirmDelete(supplier: Supplier) = setState { copy(pendingDelete = supplier) }
    fun cancelDelete() = setState { copy(pendingDelete = null) }

    fun deleteConfirmed() {
        val target = current.pendingDelete ?: return
        setState { copy(deleting = true, error = null) }
        launchResult(
            block = { deleteSupplier(target.id) },
            onSuccess = {
                setState {
                    copy(
                        deleting = false,
                        pendingDelete = null,
                        suppliers = suppliers.filterNot { s -> s.id == target.id },
                    )
                }
            },
            onFailure = { e ->
                setState {
                    copy(
                        deleting = false,
                        pendingDelete = null,
                        error = e.message ?: ErrorMessages.DELETE_FAILED,
                    )
                }
            },
        )
    }
}
