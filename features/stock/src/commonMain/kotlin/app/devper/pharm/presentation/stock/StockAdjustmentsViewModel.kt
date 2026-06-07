package app.devper.pharm.presentation.stock

import app.devper.pharm.common.error.ErrorMessages
import app.devper.pharm.domain.model.AdjustmentReason
import app.devper.pharm.domain.param.AddStockAdjustmentParam
import app.devper.pharm.domain.usecase.AddStockAdjustmentUseCase
import app.devper.pharm.domain.usecase.GetStockAdjustmentsUseCase
import app.devper.pharm.ui.common.BaseLoadableViewModel

private const val LOAD_HISTORY_FAILED = "โหลดประวัติไม่สำเร็จ"

class StockAdjustmentsViewModel(
    private val getAdjustments: GetStockAdjustmentsUseCase,
    private val addAdjustment: AddStockAdjustmentUseCase,
) : BaseLoadableViewModel<StockAdjustmentsUiState>(StockAdjustmentsUiState()) {

    fun open(drugId: String, drugName: String) {
        setState {
            copy(
                drugId = drugId,
                drugName = drugName,
                addFormOpen = false,
                draft = AdjustmentDraft(),
                error = null,
            )
        }
        reload()
    }

    fun close() {
        setState { StockAdjustmentsUiState() }
    }

    fun reload() {
        if (current.drugId.isBlank()) return
        val id = current.drugId
        launchLoad(
            block = { getAdjustments(id) },
            fallback = LOAD_HISTORY_FAILED,
            onSuccess = { list -> copy(history = list) },
        )
    }

    fun toggleAddForm() = setState {
        copy(addFormOpen = !addFormOpen, draft = AdjustmentDraft())
    }

    fun onSign(v: AdjustmentSign) = patch { copy(sign = v) }
    fun onAbsDelta(v: String) = patch { copy(absDelta = v.filter { c -> c.isDigit() }) }
    fun onReason(v: AdjustmentReason) = patch { copy(reason = v) }
    fun onNote(v: String) = patch { copy(note = v) }

    fun submitAdd() {
        val s = current
        if (!s.canSubmitDraft) return
        val signed = s.signedDelta()
        if (signed == 0) return
        setState { copy(saving = true, error = null) }
        launchResult(
            block = {
                addAdjustment(
                    AddStockAdjustmentParam(
                        drugId = s.drugId,
                        delta = signed,
                        reason = s.draft.reason,
                        note = s.draft.note,
                    ),
                )
            },
            onSuccess = {
                setState { copy(saving = false, addFormOpen = false, draft = AdjustmentDraft()) }
                reload()
            },
            onFailure = { e -> setState { copy(saving = false, error = e.message ?: ErrorMessages.SAVE_FAILED) } },
        )
    }

    private fun patch(transform: AdjustmentDraft.() -> AdjustmentDraft) {
        setState { copy(draft = draft.transform()) }
    }
}
