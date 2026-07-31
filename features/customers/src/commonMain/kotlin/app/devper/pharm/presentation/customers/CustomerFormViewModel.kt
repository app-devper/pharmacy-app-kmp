package app.devper.pharm.presentation.customers

import app.devper.pharm.domain.param.customers.CustomerInput
import app.devper.pharm.domain.param.customers.UpdateCustomerParam
import app.devper.pharm.domain.usecase.customers.AddCustomerUseCase
import app.devper.pharm.domain.usecase.customers.GetCustomersUseCase
import app.devper.pharm.domain.usecase.customers.UpdateCustomerUseCase
import app.devper.pharm.presentation.customers.exception.CustomerFormUiStateError
import app.devper.pharm.ui.common.BaseFormViewModel

class CustomerFormViewModel(
    private val getCustomers: GetCustomersUseCase,
    private val addCustomer: AddCustomerUseCase,
    private val updateCustomer: UpdateCustomerUseCase,
) : BaseFormViewModel<CustomerFormUiState>(CustomerFormUiState()) {

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
        return when (val mode = current.mode) {
            is CustomerFormMode.Add  -> addCustomer(input).map { Unit }
            is CustomerFormMode.Edit -> updateCustomer(UpdateCustomerParam(id = mode.customerId, input = input))
        }
    }

    private fun hydrateForEdit(id: String) {
        setState { copy(loading = true, errorState = null) }
        launchResult(
            block = { getCustomers() },
            onSuccess = { list ->
                val c = list.firstOrNull { it.id == id }
                if (c == null) {
                    setState { copy(loading = false, errorState = CustomerFormUiStateError.NotFound()) }
                } else {
                    val hydratedForm = CustomerFormFields(
                        name = c.name,
                        phone = c.phone.orEmpty(),
                        allergyNote = c.allergyNote.orEmpty(),
                        priceTier = c.priceTier,
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
            onFailure = { e -> setState { copy(loading = false, errorState = CustomerFormUiStateError.LoadCustomerFailed(e)) } },
        )
    }

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
