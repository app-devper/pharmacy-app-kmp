package app.devper.pharm.presentation.stock

import app.devper.pharm.common.value.Money
import app.devper.pharm.common.value.Quantity

import app.devper.pharm.domain.model.DrugLot
import app.devper.pharm.domain.param.AddLotParam
import app.devper.pharm.domain.param.DeleteLotParam
import app.devper.pharm.domain.usecase.AddLotUseCase
import app.devper.pharm.domain.usecase.DeleteLotUseCase
import app.devper.pharm.domain.usecase.ListLotsUseCase
import app.devper.pharm.ui.common.BaseLoadableViewModel
import app.devper.pharm.ui.format.toLocalDateOrNull

private const val LOAD_LOTS_FAILED = "โหลดล็อตไม่สำเร็จ"

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
                error = null,
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
        launchLoad(
            block = { listLots(id) },
            fallback = LOAD_LOTS_FAILED,
            onSuccess = { lots -> copy(lots = lots) },
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
            setState { copy(error = "วันหมดอายุไม่ถูกต้อง (รูปแบบ YYYY-MM-DD)") }
            return
        }
        setState { copy(saving = true, error = null) }
        val param = AddLotParam(
            drugId = s.drugId,
            lotNumber = s.draft.lotNumber.trim(),
            expiryDate = parsedExpiry,
            costPrice = s.draft.costPrice.toDoubleOrNull(),
            sellPrice = s.draft.sellPrice.toDoubleOrNull(),
            quantity = s.draft.quantity.toIntOrNull() ?: 0,
        )
        launchResult(
            block = { addLot(param) },
            onSuccess = {
                setState { copy(saving = false, addFormOpen = false, draft = LotDraft()) }
                reload()
            },
            onFailure = { e -> setState { copy(saving = false, error = e.message ?: "เพิ่มล็อตไม่สำเร็จ") } },
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
            onFailure = { e -> setState { copy(saving = false, error = e.message ?: "ลบล็อตไม่สำเร็จ") } },
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
