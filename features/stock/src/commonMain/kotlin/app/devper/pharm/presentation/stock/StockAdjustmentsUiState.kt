package app.devper.pharm.presentation.stock

import app.devper.pharm.domain.model.AdjustmentReason
import app.devper.pharm.domain.model.StockAdjustment
import app.devper.pharm.ui.common.BaseUiState

data class AdjustmentDraft(
    val sign: AdjustmentSign = AdjustmentSign.Decrease,
    val absDelta: String = "",
    val reason: AdjustmentReason = AdjustmentReason.Recount,
    val note: String = "",
)

enum class AdjustmentSign { Increase, Decrease }

data class StockAdjustmentsUiState(
    val drugId: String = "",
    val drugName: String = "",
    val history: List<StockAdjustment> = emptyList(),
    override val loading: Boolean = false,
    val addFormOpen: Boolean = false,
    val draft: AdjustmentDraft = AdjustmentDraft(),
    val saving: Boolean = false,
    override val error: String? = null,
) : BaseUiState {

    val canSubmitDraft: Boolean
        get() = !saving && (draft.absDelta.toIntOrNull() ?: 0) > 0

    fun signedDelta(): Int {
        val abs = draft.absDelta.toIntOrNull() ?: 0
        return if (draft.sign == AdjustmentSign.Decrease) -abs else abs
    }
}
