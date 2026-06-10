package app.devper.pharm.presentation.stock

import app.devper.pharm.domain.model.DrugLot
import app.devper.pharm.common.AppException
import app.devper.pharm.ui.common.LoadableUiState

data class LotDraft(
    val lotNumber: String = "",
    val expiryDate: String = "",
    val quantity: String = "",
    val costPrice: String = "",
    val sellPrice: String = "",
)

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
        get() = !saving &&
            draft.lotNumber.isNotBlank() &&
            draft.expiryDate.isNotBlank() &&
            (draft.quantity.toIntOrNull() ?: 0) > 0
}
