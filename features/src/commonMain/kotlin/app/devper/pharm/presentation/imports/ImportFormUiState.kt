package app.devper.pharm.presentation.imports

import app.devper.pharm.domain.model.Drug
import app.devper.pharm.domain.model.Supplier
import app.devper.pharm.domain.parser.PurchaseOrderInputBuilder
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
    val drugs: List<Drug> = emptyList(),
    val suppliers: List<Supplier> = emptyList(),
    val readOnly: Boolean = false,
    override val loading: Boolean = false,
    override val saving: Boolean = false,
    override val saved: Boolean = false,
    override val error: String? = null,
) : BaseFormUiState<ImportFormUiState> {
    override val canSubmit: Boolean
        get() = !saving && !loading && !readOnly &&
            form.supplier.isNotBlank() &&
            form.items.isNotEmpty() &&
            form.items.all {
                PurchaseOrderInputBuilder.isLineValid(
                    drugId = it.drugId,
                    lotNumber = it.lotNumber,
                    expiryDate = it.expiryDate,
                    qty = it.qty,
                )
            }

    val titleLabel: String
        get() = if (mode is ImportFormMode.Edit) "แก้ไขใบรับสินค้า" else "ใบรับสินค้าใหม่"

    override fun withSaving(saving: Boolean) = copy(saving = saving)
    override fun withSaved(saved: Boolean) = copy(saved = saved)
    override fun withError(error: String?) = copy(error = error)
}
