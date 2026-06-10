package app.devper.pharm.presentation.stock

import app.devper.pharm.common.error.CommonUiStateError
import app.devper.pharm.presentation.stock.exception.StockUiStateError
import app.devper.pharm.domain.model.AdjustmentReason
import app.devper.pharm.domain.param.AddStockAdjustmentParam
import app.devper.pharm.domain.usecase.inventory.AddStockAdjustmentUseCase
import app.devper.pharm.domain.usecase.inventory.GetStockAdjustmentsUseCase
import app.devper.pharm.ui.common.BaseLoadableViewModel


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
                errorState = null,
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
        setState { copy(loading = true, errorState = null) }
        launchResult(
            block = { getAdjustments(id) },
            onSuccess = { list -> setState { copy(loading = false, history = list) } },
            onFailure = { e -> setState { copy(loading = false, errorState = StockUiStateError.LoadHistoryFailed(e)) } },
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
        setState { copy(saving = true, errorState = null) }
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
            onFailure = { e -> setState { copy(saving = false, errorState = CommonUiStateError.SaveFailed(e)) } },
        )
    }

    private fun patch(transform: AdjustmentDraft.() -> AdjustmentDraft) {
        setState { copy(draft = draft.transform()) }
    }
}
