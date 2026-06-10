package app.devper.pharm.presentation.stock

import app.devper.pharm.presentation.stock.exception.DrugLotsUiStateError

import app.devper.pharm.common.value.Money
import app.devper.pharm.common.value.Quantity

import app.devper.pharm.domain.model.DrugLot
import app.devper.pharm.domain.param.AddLotParam
import app.devper.pharm.domain.param.DeleteLotParam
import app.devper.pharm.domain.usecase.inventory.AddLotUseCase
import app.devper.pharm.domain.usecase.inventory.DeleteLotUseCase
import app.devper.pharm.domain.usecase.inventory.ListLotsUseCase
import app.devper.pharm.ui.common.BaseLoadableViewModel
import app.devper.pharm.ui.format.toLocalDateOrNull


class DrugLotsViewModel(
    private val listLots: ListLotsUseCase,
    private val addLot: AddLotUseCase,
    private val deleteLot: DeleteLotUseCase,
) : BaseLoadableViewModel<DrugLotsUiState>(DrugLotsUiState()) {

    fun open(drugId: String, drugName: String) {
        setState {
            copy(
                drugId = drugId,
                drugName = drugName,
                addFormOpen = false,
                draft = LotDraft(),
                errorState = null,
            )
        }
        reload()
    }

    fun close() {
        setState { DrugLotsUiState() }
    }

    fun reload() {
        if (current.drugId.isBlank()) return
        val id = current.drugId
        setState { copy(loading = true, errorState = null) }
        launchResult(
            block = { listLots(id) },
            onSuccess = { lots -> setState { copy(loading = false, lots = lots) } },
            onFailure = { e -> setState { copy(loading = false, errorState = DrugLotsUiStateError.LoadLotsFailed(e)) } },
        )
    }

    fun toggleAddForm() = setState { copy(addFormOpen = !addFormOpen, draft = LotDraft()) }
    fun onLotNumber(v: String) = setState { copy(draft = draft.copy(lotNumber = v)) }
    fun onExpiryDate(v: String) = setState { copy(draft = draft.copy(expiryDate = v)) }
    fun onQuantity(v: String) =
        setState { copy(draft = draft.copy(quantity = v.filter { c -> c.isDigit() })) }
    fun onCostPrice(v: String) =
        setState { copy(draft = draft.copy(costPrice = v.numericOnly())) }
    fun onSellPrice(v: String) =
        setState { copy(draft = draft.copy(sellPrice = v.numericOnly())) }

    fun submitAdd() {
        val s = current
        if (!s.canSubmitDraft) return
        val parsedExpiry = s.draft.expiryDate.trim().toLocalDateOrNull()
        if (parsedExpiry == null) {
            setState { copy(errorState = DrugLotsUiStateError.InvalidExpiry()) }
            return
        }
        setState { copy(saving = true, errorState = null) }
        val param = AddLotParam(
            drugId = s.drugId,
            lotNumber = s.draft.lotNumber.trim(),
            expiryDate = parsedExpiry,
            costPrice = s.draft.costPrice.toDoubleOrNull()?.let(::Money),
            sellPrice = s.draft.sellPrice.toDoubleOrNull()?.let(::Money),
            quantity = Quantity(s.draft.quantity.toIntOrNull() ?: 0),
        )
        launchResult(
            block = { addLot(param) },
            onSuccess = {
                setState { copy(saving = false, addFormOpen = false, draft = LotDraft()) }
                reload()
            },
            onFailure = { e -> setState { copy(saving = false, errorState = DrugLotsUiStateError.AddLotFailed(e)) } },
        )
    }

    fun requestDelete(lot: DrugLot) = setState { copy(pendingDelete = lot) }
    fun cancelDelete() = setState { copy(pendingDelete = null) }
    fun confirmDelete() {
        val s = current
        val lot = s.pendingDelete ?: return
        setState { copy(pendingDelete = null, saving = true) }
        launchResult(
            block = { deleteLot(DeleteLotParam(drugId = s.drugId, lotId = lot.id)) },
            onSuccess = {
                setState { copy(saving = false) }
                reload()
            },
            onFailure = { e -> setState { copy(saving = false, errorState = DrugLotsUiStateError.RemoveLotFailed(e)) } },
        )
    }
}

private fun String.numericOnly(): String {
    var seenDot = false
    val sb = StringBuilder(length)
    for (c in this) when {
        c.isDigit()           -> sb.append(c)
        c == '.' && !seenDot  -> { sb.append('.'); seenDot = true }
        else                  -> {}
    }
    return sb.toString()
}
