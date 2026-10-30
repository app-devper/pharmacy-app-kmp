package app.devper.pharm.presentation.customers

import app.devper.pharm.domain.repository.customers.CustomerRepository
import app.devper.pharm.domain.param.customers.CustomerInput
import app.devper.pharm.presentation.customers.exception.CustomerFormUiStateError
import app.devper.pharm.ui.common.BaseEditFormViewModel

class CustomerFormViewModel(
    private val customers: CustomerRepository,
) : BaseEditFormViewModel<CustomerFormUiState, CustomerFormFields>(CustomerFormUiState()) {

    fun init(mode: CustomerFormMode) {
        setState { copy(mode = mode) }
        if (mode is CustomerFormMode.Edit) hydrateForEdit(mode.customerId)
    }

    fun onName(v: String) = patch { copy(name = v) }
    fun onPhone(v: String) = patch { copy(phone = v) }
    fun onAllergyNote(v: String) = patch { copy(allergyNote = v) }
    fun onPriceTier(v: String) = patch { copy(priceTier = v) }

    override suspend fun persist(): Result<Unit> {
        val input = current.form.toInput()
        when (val mode = current.mode) {
            is CustomerFormMode.Add  -> customers.add(input)
            is CustomerFormMode.Edit -> customers.update(mode.customerId, input)
        }
        return Result.success(Unit)
    }

    private fun hydrateForEdit(id: String) = hydrate(
        fetch = { customers.list().firstOrNull { it.id == id } },
        notFound = { CustomerFormUiStateError.NotFound() },
        failure = { e -> CustomerFormUiStateError.LoadCustomerFailed(e) },
        toForm = { c ->
            CustomerFormFields(
                name = c.name,
                phone = c.phone.orEmpty(),
                allergyNote = c.allergyNote.orEmpty(),
                priceTier = c.priceTier,
            )
        },
    )

    private fun patch(transform: CustomerFormFields.() -> CustomerFormFields) {
        setState { copy(form = form.transform()) }
    }

    private fun CustomerFormFields.toInput() = CustomerInput(
        name = name.trim(),
        phone = phone.trim(),
        allergyNote = allergyNote.trim(),
        priceTier = priceTier.trim(),
    )
}
