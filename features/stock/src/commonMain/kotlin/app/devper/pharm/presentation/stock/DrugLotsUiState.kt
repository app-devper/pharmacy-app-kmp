package app.devper.pharm.presentation.stock

import app.devper.pharm.domain.model.DrugLot
import app.devper.pharm.common.AppException
import app.devper.pharm.ui.common.LoadableUiState
import kotlinx.datetime.LocalDate

data class LotDraft(
    val lotNumber: String = "",
    val expiryDate: String = "",
    val quantity: String = "",
    val costPrice: String = "",
    val sellPrice: String = "",
) {
    val lotNumberValid: Boolean get() = lotNumber.isNotBlank()
    val expiryDateValid: Boolean get() = runCatching { LocalDate.parse(expiryDate.trim()) }.isSuccess
    val quantityValid: Boolean get() = (quantity.toIntOrNull() ?: 0) > 0
    val costPriceValid: Boolean get() = costPrice.isValidOptionalPrice()
    val sellPriceValid: Boolean get() = sellPrice.isValidOptionalPrice()
    val valid: Boolean
        get() = lotNumberValid && expiryDateValid && quantityValid && costPriceValid && sellPriceValid
}

data class DrugLotsUiState(
    val drugId: String = "",
    val drugName: String = "",
    val lots: List<DrugLot> = emptyList(),
    override val loading: Boolean = false,
    val addFormOpen: Boolean = false,
    val draft: LotDraft = LotDraft(),
    val saving: Boolean = false,
    val pendingDelete: DrugLot? = null,
    val errorState: AppException? = null,
) : LoadableUiState<DrugLotsUiState> {

    override fun withLoading(value: Boolean) = copy(loading = value)
    override val domainError: AppException? get() = errorState
    override fun withDomainError(error: AppException?) = copy(errorState = error)

    val canSubmitDraft: Boolean
        get() = !saving && draft.valid

    val canAttemptSubmit: Boolean get() = !saving
}

private fun String.isValidOptionalPrice(): Boolean {
    if (isBlank()) return true
    val parsed = toDoubleOrNull() ?: return false
    return parsed.isFinite() && parsed >= 0.0
}
