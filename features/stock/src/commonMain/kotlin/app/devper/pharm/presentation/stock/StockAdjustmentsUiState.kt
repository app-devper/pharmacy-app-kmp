package app.devper.pharm.presentation.stock

import app.devper.pharm.domain.model.AdjustmentReason
import app.devper.pharm.domain.model.StockAdjustment
import app.devper.pharm.common.AppException
import app.devper.pharm.ui.common.LoadableUiState

data class AdjustmentDraft(
    val sign: AdjustmentSign = AdjustmentSign.Decrease,
    val absDelta: String = "",
    val reason: AdjustmentReason = AdjustmentReason.Recount,
    val note: String = "",
) {
    val absDeltaValid: Boolean get() = (absDelta.toIntOrNull() ?: 0) > 0
}

enum class AdjustmentSign { Increase, Decrease }

data class StockAdjustmentsUiState(
    val drugId: String = "",
    val drugName: String = "",
    val history: List<StockAdjustment> = emptyList(),
    override val loading: Boolean = false,
    val addFormOpen: Boolean = false,
    val draft: AdjustmentDraft = AdjustmentDraft(),
    val saving: Boolean = false,
    val errorState: AppException? = null,
) : LoadableUiState<StockAdjustmentsUiState> {

    override fun withLoading(value: Boolean) = copy(loading = value)
    override val domainError: AppException? get() = errorState
    override fun withDomainError(error: AppException?) = copy(errorState = error)

    val canSubmitDraft: Boolean
        get() = !saving && draft.absDeltaValid

    val canAttemptSubmit: Boolean get() = !saving

    fun signedDelta(): Int {
        val abs = draft.absDelta.toIntOrNull() ?: 0
        return if (draft.sign == AdjustmentSign.Decrease) -abs else abs
    }
}
