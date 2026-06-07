package app.devper.pharm.presentation.customers

import app.devper.pharm.common.error.ErrorMessages
import app.devper.pharm.domain.param.AddCustomerParam
import app.devper.pharm.domain.param.UpdateCustomerParam
import app.devper.pharm.domain.usecase.AddCustomerUseCase
import app.devper.pharm.domain.usecase.GetCustomersUseCase
import app.devper.pharm.domain.usecase.UpdateCustomerUseCase
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
        val f = current.form
        return when (val mode = current.mode) {
            is CustomerFormMode.Add  -> addCustomer(f.toAddParam()).map { Unit }
            is CustomerFormMode.Edit -> updateCustomer(f.toUpdateParam(mode.customerId))
        }
    }

    private fun hydrateForEdit(id: String) {
        setState { copy(loading = true, error = null) }
        launchResult(
            block = { getCustomers() },
            onSuccess = { list ->
                val c = list.firstOrNull { it.id == id }
                if (c == null) {
                    setState { copy(loading = false, error = "ไม่พบลูกค้า") }
                } else {
                    setState {
                        copy(
                            loading = false,
                            form = CustomerFormFields(
                                name = c.name,
                                phone = c.phone.orEmpty(),
                                allergyNote = c.allergyNote.orEmpty(),
                                priceTier = c.priceTier,
                            ),
                        )
                    }
                }
            },
            onFailure = { e -> setState { copy(loading = false, error = e.message ?: ErrorMessages.LOAD_FAILED) } },
        )
    }

    private fun patch(transform: CustomerFormFields.() -> CustomerFormFields) {
        setState { copy(form = form.transform()) }
    }

    private fun CustomerFormFields.toAddParam() = AddCustomerParam(
        name = name.trim(),
        phone = phone.trim(),
        allergyNote = allergyNote.trim(),
        priceTier = priceTier.trim(),
    )

    private fun CustomerFormFields.toUpdateParam(id: String) = UpdateCustomerParam(
        id = id,
        name = name.trim(),
        phone = phone.trim(),
        allergyNote = allergyNote.trim(),
        priceTier = priceTier.trim(),
    )
}
