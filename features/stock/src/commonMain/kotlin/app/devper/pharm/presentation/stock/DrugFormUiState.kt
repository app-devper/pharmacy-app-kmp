package app.devper.pharm.presentation.stock

import app.devper.pharm.common.AppException
import app.devper.pharm.ui.common.BaseFormUiState
import kotlinx.datetime.LocalDate

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
) {
    val nameValid: Boolean get() = name.isNotBlank()
    val sellPriceValid: Boolean get() = sellPrice.isRequiredPriceValid()
    val costPriceValid: Boolean get() = costPrice.isOptionalPriceValid()
    val tierRetailValid: Boolean get() = tierRetail.isOptionalPriceValid()
    val tierRegularValid: Boolean get() = tierRegular.isOptionalPriceValid()
    val tierWholesaleValid: Boolean get() = tierWholesale.isOptionalPriceValid()
    val hasInitialStock: Boolean get() = (initialStock.toIntOrNull() ?: 0) > 0
    val initialLotNumberValid: Boolean get() = !hasInitialStock || lotNumber.isNotBlank()
    val initialLotExpiryValid: Boolean
        get() = !hasInitialStock || runCatching { LocalDate.parse(lotExpiry.trim()) }.isSuccess
    val initialLotCostPriceValid: Boolean get() = !hasInitialStock || lotCostPrice.isOptionalPriceValid()
    val initialLotSellPriceValid: Boolean get() = !hasInitialStock || lotSellPrice.isOptionalPriceValid()

    fun altUnitNameValid(index: Int): Boolean {
        val candidate = altUnits.getOrNull(index)?.name?.trim()?.lowercase().orEmpty()
        if (candidate.isEmpty() || candidate == unit.trim().lowercase()) return false
        return altUnits.count { it.name.trim().lowercase() == candidate } == 1
    }

    val altUnitsValid: Boolean
        get() = altUnits.indices.all { index ->
            altUnitNameValid(index) && altUnits[index].factorValid && altUnits[index].sellPriceValid
        }
}

data class AltUnitDraft(
    val name: String = "",
    val factor: String = "",
    val sellPrice: String = "",
    val barcode: String = "",
    val hidden: Boolean = false,
) {
    val factorValid: Boolean get() = (factor.toIntOrNull() ?: 0) >= 2
    val sellPriceValid: Boolean get() = sellPrice.isOptionalPriceValid()
}

data class DrugFormUiState(
    val mode: DrugFormMode = DrugFormMode.Add,
    val form: DrugFormFields = DrugFormFields(),
    val baselineForm: DrugFormFields = DrugFormFields(),
    override val loading: Boolean = false,
    override val saving: Boolean = false,
    override val saved: Boolean = false,
    val errorState: AppException? = null,
) : BaseFormUiState<DrugFormUiState> {

    override val canSubmit: Boolean
        get() = !saving && !loading &&
            form.nameValid &&
            form.sellPriceValid &&
            form.costPriceValid &&
            form.tierRetailValid &&
            form.tierRegularValid &&
            form.tierWholesaleValid &&
            form.initialLotNumberValid &&
            form.initialLotExpiryValid &&
            form.initialLotCostPriceValid &&
            form.initialLotSellPriceValid &&
            form.altUnitsValid

    override val hasUnsavedChanges: Boolean
        get() = form != baselineForm

    override fun withSaving(saving: Boolean) = copy(saving = saving)
    override fun withSaved(saved: Boolean) = copy(saved = saved)
    override val domainError: AppException? get() = errorState
    override fun withDomainError(error: AppException?) = copy(errorState = error)
}

private fun String.isRequiredPriceValid(): Boolean {
    if (isBlank()) return false
    return isParsedPriceValid()
}

private fun String.isOptionalPriceValid(): Boolean = isBlank() || isParsedPriceValid()

private fun String.isParsedPriceValid(): Boolean {
    val parsed = toDoubleOrNull() ?: return false
    return parsed.isFinite() && parsed >= 0.0
}
