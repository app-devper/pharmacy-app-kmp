package app.devper.pharm.presentation.customers

import app.devper.pharm.ui.common.BaseFormUiState

sealed interface CustomerFormMode {
    data object Add : CustomerFormMode
    data class Edit(val customerId: String) : CustomerFormMode
}

data class CustomerFormFields(
    val name: String = "",
    val phone: String = "",
    val allergyNote: String = "",
    val priceTier: String = "",
)

data class CustomerFormUiState(
    val mode: CustomerFormMode = CustomerFormMode.Add,
    val form: CustomerFormFields = CustomerFormFields(),
    override val loading: Boolean = false,
    override val saving: Boolean = false,
    override val saved: Boolean = false,
    override val error: String? = null,
) : BaseFormUiState<CustomerFormUiState> {
    override val canSubmit: Boolean
        get() = !saving && !loading && form.name.isNotBlank()

    val isEdit: Boolean
        get() = mode is CustomerFormMode.Edit

    override fun withSaving(saving: Boolean) = copy(saving = saving)
    override fun withSaved(saved: Boolean) = copy(saved = saved)
    override fun withError(error: String?) = copy(error = error)
}
