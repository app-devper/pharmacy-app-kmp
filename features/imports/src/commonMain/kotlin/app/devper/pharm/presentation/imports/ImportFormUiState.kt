package app.devper.pharm.presentation.imports

import app.devper.pharm.domain.model.Drug
import app.devper.pharm.domain.model.Supplier
import app.devper.pharm.domain.validation.buildPurchaseOrderItemInput
import app.devper.pharm.domain.validation.isPurchaseOrderLineValid
import app.devper.pharm.common.AppException
import app.devper.pharm.ui.common.BaseFormUiState

sealed interface ImportFormMode {
    data object Add : ImportFormMode
    data class Edit(val importId: String) : ImportFormMode
}

data class ImportLineFields(
    val drugId: String = "",
    val drugName: String = "",
    val lotNumber: String = "",
    val expiryDate: String = "",
    val qty: String = "",
    val costPrice: String = "",
    val sellPrice: String = "",
)

data class ImportFormFields(
    val supplier: String = "",
    val invoiceNo: String = "",
    val receiveDate: String = "",
    val notes: String = "",
    val items: List<ImportLineFields> = emptyList(),
)

data class ImportFormUiState(
    val mode: ImportFormMode = ImportFormMode.Add,
    val form: ImportFormFields = ImportFormFields(),
    val baselineForm: ImportFormFields = ImportFormFields(),
    val drugs: List<Drug> = emptyList(),
    val suppliers: List<Supplier> = emptyList(),
    val readOnly: Boolean = false,
    override val loading: Boolean = false,
    override val saving: Boolean = false,
    override val saved: Boolean = false,
    val errorState: AppException? = null,
) : BaseFormUiState<ImportFormUiState> {
    override val canSubmit: Boolean
        get() = !saving && !loading && !readOnly &&
            form.supplier.isNotBlank() &&
            form.items.isNotEmpty() &&
            form.items.all {
                isPurchaseOrderLineValid(
                    drugId = it.drugId,
                    lotNumber = it.lotNumber,
                    expiryDate = it.expiryDate,
                    qty = it.qty,
                )
            }

    override val hasUnsavedChanges: Boolean
        get() = !readOnly && form != baselineForm

    val isEdit: Boolean get() = mode is ImportFormMode.Edit

    override fun withSaving(saving: Boolean) = copy(saving = saving)
    override fun withSaved(saved: Boolean) = copy(saved = saved)
    override val domainError: AppException? get() = errorState
    override fun withDomainError(error: AppException?) = copy(errorState = error)
}
