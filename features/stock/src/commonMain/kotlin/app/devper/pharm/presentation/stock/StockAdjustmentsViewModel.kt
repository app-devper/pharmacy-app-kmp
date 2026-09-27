package app.devper.pharm.presentation.stock

import app.devper.pharm.domain.usecase.inventory.ListLotsUseCase
import app.devper.pharm.common.error.CommonUiStateError
import app.devper.pharm.presentation.stock.exception.StockUiStateError
import app.devper.pharm.domain.model.AdjustmentReason
import app.devper.pharm.domain.param.inventory.AddStockAdjustmentParam
import app.devper.pharm.domain.usecase.inventory.AddStockAdjustmentUseCase
import app.devper.pharm.domain.usecase.inventory.GetStockAdjustmentsUseCase
import app.devper.pharm.ui.common.BaseLoadableViewModel


class StockAdjustmentsViewModel(
    private val getAdjustments: GetStockAdjustmentsUseCase,
    private val addAdjustment: AddStockAdjustmentUseCase,
    private val listLots: ListLotsUseCase,
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
        launchResult(
            block = { listLots(id) },
            onSuccess = { lots ->
                val usable = lots.filterNot { it.writtenOff }.sortedByDescending { it.expiryDate }
                setState { copy(lots = usable, draft = draft.withDefaultLot(usable)) }
            },
            onFailure = { /* the form falls back to requiring nothing; the backend assumes a lot */ },
        )
    }

    private fun AdjustmentDraft.withDefaultLot(lots: List<app.devper.pharm.domain.model.DrugLot>) =
        if (lotChoice.isNotEmpty()) this else copy(lotChoice = lots.firstOrNull()?.id ?: AdjustmentDraft.NEW_LOT)

    fun toggleAddForm() = setState {
        copy(addFormOpen = !addFormOpen, draft = AdjustmentDraft().withDefaultLot(lots))
    }

    fun onLotChoice(v: String) = patch { copy(lotChoice = v) }
    fun onNewLotNumber(v: String) = patch { copy(newLotNumber = v) }
    fun onNewLotExpiry(v: String) = patch { copy(newLotExpiry = v) }

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
                        lot = s.lotTarget(),
                    ),
                )
            },
            onSuccess = {
                setState { copy(saving = false, addFormOpen = false, draft = AdjustmentDraft().withDefaultLot(lots)) }
                reload()
            },
            onFailure = { e -> setState { copy(saving = false, errorState = CommonUiStateError.SaveFailed(e)) } },
        )
    }

    private fun patch(transform: AdjustmentDraft.() -> AdjustmentDraft) {
        setState { copy(draft = draft.transform()) }
    }
}
