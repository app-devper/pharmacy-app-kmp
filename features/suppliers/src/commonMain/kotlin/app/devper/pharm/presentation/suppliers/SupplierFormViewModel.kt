package app.devper.pharm.presentation.suppliers

import app.devper.pharm.domain.param.suppliers.SupplierInput
import app.devper.pharm.domain.param.suppliers.UpdateSupplierParam
import app.devper.pharm.domain.usecase.suppliers.AddSupplierUseCase
import app.devper.pharm.domain.usecase.suppliers.GetSuppliersUseCase
import app.devper.pharm.domain.usecase.suppliers.UpdateSupplierUseCase
import app.devper.pharm.presentation.suppliers.exception.SupplierFormUiStateError
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
        val input = current.form.toInput()
        return when (val mode = current.mode) {
            is SupplierFormMode.Add  -> addSupplier(input).map { Unit }
            is SupplierFormMode.Edit -> updateSupplier(UpdateSupplierParam(id = mode.supplierId, input = input))
        }
    }

    private fun hydrateForEdit(id: String) {
        setState { copy(loading = true, errorState = null) }
        launchResult(
            block = { getSuppliers() },
            onSuccess = { list ->
                val s = list.firstOrNull { it.id == id }
                if (s == null) {
                    setState { copy(loading = false, errorState = SupplierFormUiStateError.NotFound()) }
                } else {
                    val hydratedForm = SupplierFormFields(
                        name = s.name,
                        contactName = s.contactName,
                        phone = s.phone,
                        address = s.address,
                        taxId = s.taxId,
                        notes = s.notes,
                    )
                    setState {
                        copy(
                            loading = false,
                            form = hydratedForm,
                            baselineForm = hydratedForm,
                        )
                    }
                }
            },
            onFailure = { e ->
                setState { copy(loading = false, errorState = SupplierFormUiStateError.LoadSupplierFailed(e)) }
            },
        )
    }

    private fun patch(transform: SupplierFormFields.() -> SupplierFormFields) {
        setState { copy(form = form.transform()) }
    }

    private fun SupplierFormFields.toInput() = SupplierInput(
        name = name.trim(),
        contactName = contactName.trim(),
        phone = phone.trim(),
        address = address.trim(),
        taxId = taxId.trim(),
        notes = notes.trim(),
    )
}
