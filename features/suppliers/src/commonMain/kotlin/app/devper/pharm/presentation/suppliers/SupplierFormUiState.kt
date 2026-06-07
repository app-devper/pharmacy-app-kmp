package app.devper.pharm.presentation.suppliers

import app.devper.pharm.ui.common.BaseFormUiState

sealed interface SupplierFormMode {
    data object Add : SupplierFormMode
    data class Edit(val supplierId: String) : SupplierFormMode
}

data class SupplierFormFields(
    val name: String = "",
    val contactName: String = "",
    val phone: String = "",
    val address: String = "",
    val taxId: String = "",
    val notes: String = "",
)

data class SupplierFormUiState(
    val mode: SupplierFormMode = SupplierFormMode.Add,
    val form: SupplierFormFields = SupplierFormFields(),
    override val loading: Boolean = false,
    override val saving: Boolean = false,
    override val saved: Boolean = false,
    override val error: String? = null,
) : BaseFormUiState<SupplierFormUiState> {
    override val canSubmit: Boolean
        get() = !saving && !loading && form.name.isNotBlank()

    val isEdit: Boolean
        get() = mode is SupplierFormMode.Edit

    override fun withSaving(saving: Boolean) = copy(saving = saving)
    override fun withSaved(saved: Boolean) = copy(saved = saved)
    override fun withError(error: String?) = copy(error = error)
}
