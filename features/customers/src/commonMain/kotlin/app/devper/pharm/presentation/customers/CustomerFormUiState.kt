package app.devper.pharm.presentation.customers

import app.devper.pharm.common.AppException
import app.devper.pharm.ui.common.EditFormUiState

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
    val baselineForm: CustomerFormFields = CustomerFormFields(),
    override val loading: Boolean = false,
    override val saving: Boolean = false,
    override val saved: Boolean = false,
    val errorState: AppException? = null,
) : EditFormUiState<CustomerFormUiState, CustomerFormFields> {
    override val canSubmit: Boolean
        get() = !saving && !loading && form.name.isNotBlank()

    override val hasUnsavedChanges: Boolean
        get() = form != baselineForm

    val isEdit: Boolean
        get() = mode is CustomerFormMode.Edit

    override fun withSaving(saving: Boolean) = copy(saving = saving)
    override fun withLoading(loading: Boolean) = copy(loading = loading)
    override fun withHydrated(form: CustomerFormFields) = copy(form = form, baselineForm = form)
    override fun withSaved(saved: Boolean) = copy(saved = saved)
    override val domainError: AppException? get() = errorState
    override fun withDomainError(error: AppException?) = copy(errorState = error)
}
