package app.devper.pharm.presentation.stock

import app.devper.pharm.domain.model.LotChoice
import app.devper.pharm.domain.model.LotTarget
import app.devper.pharm.domain.model.DrugLot
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
    val lot: LotChoice? = null,
    val lotsUnknown: Boolean = false,
    override val loading: Boolean = false,
    val addFormOpen: Boolean = false,
    val draft: AdjustmentDraft = AdjustmentDraft(),
    val saving: Boolean = false,
    val errorState: AppException? = null,
) : LoadableUiState<StockAdjustmentsUiState> {

    override fun withLoading(value: Boolean) = copy(loading = value)
    override val domainError: AppException? get() = errorState
    override fun withDomainError(error: AppException?) = copy(errorState = error)

    val lots: List<DrugLot> get() = lot?.lots.orEmpty()

    val canSubmitDraft: Boolean
        get() = !saving && draft.absDeltaValid &&
            !(draft.sign == AdjustmentSign.Increase && lotsUnknown) &&
            (!lotNeeded || lotTarget() != null)

    val lotNeeded: Boolean get() = draft.sign == AdjustmentSign.Increase && lot != null

    fun lotTarget(): LotTarget? = if (lotNeeded) lot?.target else null

    val canAttemptSubmit: Boolean get() = !saving

    fun signedDelta(): Int {
        val abs = draft.absDelta.toIntOrNull() ?: 0
        return if (draft.sign == AdjustmentSign.Decrease) -abs else abs
    }
}
