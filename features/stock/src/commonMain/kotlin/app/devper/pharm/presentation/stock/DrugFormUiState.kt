package app.devper.pharm.presentation.stock

import app.devper.pharm.ui.common.BaseFormUiState

sealed interface DrugFormMode {
    data object Add : DrugFormMode
    data class Edit(val drugId: String) : DrugFormMode
}

data class DrugFormFields(
    val name: String = "",
    val genericName: String = "",
    val type: String = "",
    val strength: String = "",
    val barcode: String = "",
    val regNo: String = "",
    val unit: String = "ชิ้น",
    val sellPrice: String = "",
    val costPrice: String = "",
    val minStock: String = "",
    val tierRetail: String = "",
    val tierRegular: String = "",
    val tierWholesale: String = "",
    val altUnits: List<AltUnitDraft> = emptyList(),
    val reportTypes: Set<String> = emptySet(),

    val initialStock: String = "",
    val lotNumber: String = "",
    val lotExpiry: String = "",
    val lotQty: String = "",
    val lotCostPrice: String = "",
    val lotSellPrice: String = "",
)

data class AltUnitDraft(
    val name: String = "",
    val factor: String = "",
    val sellPrice: String = "",
    val barcode: String = "",
    val hidden: Boolean = false,
)

data class DrugFormUiState(
    val mode: DrugFormMode = DrugFormMode.Add,
    val form: DrugFormFields = DrugFormFields(),
    override val loading: Boolean = false,
    override val saving: Boolean = false,
    override val saved: Boolean = false,
    override val error: String? = null,
) : BaseFormUiState<DrugFormUiState> {

    override val canSubmit: Boolean
        get() = !saving && !loading &&
            form.name.isNotBlank() &&
            (form.sellPrice.toDoubleOrNull() ?: -1.0) >= 0 &&
            form.altUnits.all { (it.factor.toIntOrNull() ?: 0) >= 2 } &&

            form.altUnits.map { it.name.trim().lowercase() }
                .let { names -> names.all { it.isNotEmpty() } && names.size == names.toSet().size } &&
            form.altUnits.none { it.name.trim().equals(form.unit.trim(), ignoreCase = true) }

    val titleLabel: String
        get() = if (mode is DrugFormMode.Edit) "แก้ไขยา" else "เพิ่มยา"

    override fun withSaving(saving: Boolean) = copy(saving = saving)
    override fun withSaved(saved: Boolean) = copy(saved = saved)
    override fun withError(error: String?) = copy(error = error)
}
