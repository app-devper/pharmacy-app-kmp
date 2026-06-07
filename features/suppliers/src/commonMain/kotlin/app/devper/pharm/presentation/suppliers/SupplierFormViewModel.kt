package app.devper.pharm.presentation.suppliers

import app.devper.pharm.common.error.ErrorMessages

import app.devper.pharm.domain.param.AddSupplierParam
import app.devper.pharm.domain.param.UpdateSupplierParam
import app.devper.pharm.domain.usecase.AddSupplierUseCase
import app.devper.pharm.domain.usecase.GetSuppliersUseCase
import app.devper.pharm.domain.usecase.UpdateSupplierUseCase
import app.devper.pharm.ui.common.BaseFormViewModel

class SupplierFormViewModel(
    private val getSuppliers: GetSuppliersUseCase,
    private val addSupplier: AddSupplierUseCase,
    private val updateSupplier: UpdateSupplierUseCase,
) : BaseFormViewModel<SupplierFormUiState>(SupplierFormUiState()) {

    fun init(mode: SupplierFormMode) {
        setState { copy(mode = mode) }
        if (mode is SupplierFormMode.Edit) hydrateForEdit(mode.supplierId)
    }

    fun onName(v: String) = patch { copy(name = v) }
    fun onContactName(v: String) = patch { copy(contactName = v) }
    fun onPhone(v: String) = patch { copy(phone = v) }
    fun onAddress(v: String) = patch { copy(address = v) }
    fun onTaxId(v: String) = patch { copy(taxId = v) }
    fun onNotes(v: String) = patch { copy(notes = v) }

    override suspend fun persist(): Result<Unit> {
        val f = current.form
        return when (val mode = current.mode) {
            is SupplierFormMode.Add  -> addSupplier(f.toAddParam()).map { Unit }
            is SupplierFormMode.Edit -> updateSupplier(f.toUpdateParam(mode.supplierId))
        }
    }

    private fun hydrateForEdit(id: String) {
        setState { copy(loading = true, error = null) }
        launchResult(
            block = { getSuppliers() },
            onSuccess = { list ->
                val s = list.firstOrNull { it.id == id }
                if (s == null) {
                    setState { copy(loading = false, error = "ไม่พบผู้จัดจำหน่าย") }
                } else {
                    setState {
                        copy(
                            loading = false,
                            form = SupplierFormFields(
                                name = s.name,
                                contactName = s.contactName,
                                phone = s.phone,
                                address = s.address,
                                taxId = s.taxId,
                                notes = s.notes,
                            ),
                        )
                    }
                }
            },
            onFailure = { e -> setState { copy(loading = false, error = e.message ?: ErrorMessages.LOAD_FAILED) } },
        )
    }

    private fun patch(transform: SupplierFormFields.() -> SupplierFormFields) {
        setState { copy(form = form.transform()) }
    }

    private fun SupplierFormFields.toAddParam() = AddSupplierParam(
        name = name.trim(),
        contactName = contactName.trim(),
        phone = phone.trim(),
        address = address.trim(),
        taxId = taxId.trim(),
        notes = notes.trim(),
    )

    private fun SupplierFormFields.toUpdateParam(id: String) = UpdateSupplierParam(
        id = id,
        name = name.trim(),
        contactName = contactName.trim(),
        phone = phone.trim(),
        address = address.trim(),
        taxId = taxId.trim(),
        notes = notes.trim(),
    )
}
