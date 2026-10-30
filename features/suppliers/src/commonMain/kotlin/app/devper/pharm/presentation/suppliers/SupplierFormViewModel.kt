package app.devper.pharm.presentation.suppliers

import app.devper.pharm.domain.repository.suppliers.SupplierRepository
import app.devper.pharm.domain.param.suppliers.SupplierInput
import app.devper.pharm.presentation.suppliers.exception.SupplierFormUiStateError
import app.devper.pharm.ui.common.BaseEditFormViewModel

class SupplierFormViewModel(
    private val suppliers: SupplierRepository,
) : BaseEditFormViewModel<SupplierFormUiState, SupplierFormFields>(SupplierFormUiState()) {

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
        when (val mode = current.mode) {
            is SupplierFormMode.Add  -> suppliers.add(input)
            is SupplierFormMode.Edit -> suppliers.update(mode.supplierId, input)
        }
        return Result.success(Unit)
    }

    private fun hydrateForEdit(id: String) = hydrate(
        fetch = { suppliers.list().firstOrNull { it.id == id } },
        notFound = { SupplierFormUiStateError.NotFound() },
        failure = { e -> SupplierFormUiStateError.LoadSupplierFailed(e) },
        toForm = { s ->
            SupplierFormFields(
                name = s.name,
                contactName = s.contactName,
                phone = s.phone,
                address = s.address,
                taxId = s.taxId,
                notes = s.notes,
            )
        },
    )

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
