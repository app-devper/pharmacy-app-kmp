package app.devper.pharm.presentation.stock

import kotlinx.datetime.LocalDate
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
    val lotChoice: String = "",
    val newLotNumber: String = "",
    val newLotExpiry: String = "",
) {
    val absDeltaValid: Boolean get() = (absDelta.toIntOrNull() ?: 0) > 0

    companion object {
        const val NEW_LOT = "new"
    }
}

enum class AdjustmentSign { Increase, Decrease }

data class StockAdjustmentsUiState(
    val drugId: String = "",
    val drugName: String = "",
    val history: List<StockAdjustment> = emptyList(),
    val lots: List<DrugLot> = emptyList(),
    override val loading: Boolean = false,
    val addFormOpen: Boolean = false,
    val draft: AdjustmentDraft = AdjustmentDraft(),
    val saving: Boolean = false,
    val errorState: AppException? = null,
) : LoadableUiState<StockAdjustmentsUiState> {

    override val domainError: AppException? get() = errorState
    override fun withDomainError(error: AppException?) = copy(errorState = error)

    val canSubmitDraft: Boolean
        get() = !saving && draft.absDeltaValid && lotTarget().let { lotNeeded == false || it != null }

    val lotNeeded: Boolean get() = draft.sign == AdjustmentSign.Increase && lots.isNotEmpty()

    fun lotTarget(): LotTarget? {
        if (!lotNeeded) return null
        return when (draft.lotChoice) {
            "" -> null
            AdjustmentDraft.NEW_LOT -> {
                val expiry = runCatching { LocalDate.parse(draft.newLotExpiry.trim()) }.getOrNull()
                if (draft.newLotNumber.isBlank() || expiry == null) null else LotTarget.New(draft.newLotNumber.trim(), expiry)
            }
            else -> LotTarget.Existing(draft.lotChoice)
        }
    }

    val canAttemptSubmit: Boolean get() = !saving

    fun signedDelta(): Int {
        val abs = draft.absDelta.toIntOrNull() ?: 0
        return if (draft.sign == AdjustmentSign.Decrease) -abs else abs
    }
}
