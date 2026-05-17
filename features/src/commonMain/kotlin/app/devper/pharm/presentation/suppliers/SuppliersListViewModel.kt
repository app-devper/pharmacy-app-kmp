package app.devper.pharm.presentation.suppliers

import app.devper.pharm.domain.model.Supplier
import app.devper.pharm.domain.usecase.DeleteSupplierUseCase
import app.devper.pharm.domain.usecase.GetSuppliersUseCase
import app.devper.pharm.ui.common.BaseViewModel

class SuppliersListViewModel(
    private val getSuppliers: GetSuppliersUseCase,
    private val deleteSupplier: DeleteSupplierUseCase,
) : BaseViewModel<SuppliersListUiState>(SuppliersListUiState()) {

    init { reload() }

    fun onQueryChange(value: String) = setState { copy(query = value) }
    fun dismissError() = setState { copy(error = null) }

    fun reload() {
        setState { copy(loading = true, error = null) }
        launchResult(
            block = { getSuppliers() },
            onSuccess = { list -> setState { copy(loading = false, suppliers = list) } },
            onFailure = { e -> setState { copy(loading = false, error = e.message ?: "โหลดข้อมูลไม่สำเร็จ") } },
        )
    }

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
                        error = e.message ?: "ลบไม่สำเร็จ",
                    )
                }
            },
        )
    }
}
