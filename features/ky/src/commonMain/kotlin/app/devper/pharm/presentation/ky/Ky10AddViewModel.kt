package app.devper.pharm.presentation.ky

import app.devper.pharm.common.error.ErrorMessages

import app.devper.pharm.domain.extension.buildKy10Draft
import app.devper.pharm.domain.extension.isKy10DraftValid
import app.devper.pharm.domain.usecase.AddKy10UseCase
import app.devper.pharm.ui.common.BaseViewModel

class Ky10AddViewModel(
    private val addKy10: AddKy10UseCase,
) : BaseViewModel<Ky10AddUiState>(Ky10AddUiState()) {

    fun onDate(v: String) = patch { copy(date = v) }
    fun onDrugName(v: String) = patch { copy(drugName = v) }
    fun onRegNo(v: String) = patch { copy(regNo = v) }
    fun onQty(v: String) = patch { copy(qty = v.filter { it.isDigit() }) }
    fun onUnit(v: String) = patch { copy(unit = v) }
    fun onBuyerName(v: String) = patch { copy(buyerName = v) }
    fun onBuyerAddress(v: String) = patch { copy(buyerAddress = v) }
    fun onRxNo(v: String) = patch { copy(rxNo = v) }
    fun onDoctor(v: String) = patch { copy(doctor = v) }
    fun onBalance(v: String) = patch { copy(balance = v.filter { it.isDigit() }) }

    fun submitAdd() {
        val s = current
        if (!s.canSubmitDraft) return
        val form = buildKy10Draft(
            date = s.draft.date,
            drugName = s.draft.drugName,
            regNo = s.draft.regNo,
            qty = s.draft.qty,
            unit = s.draft.unit,
            buyerName = s.draft.buyerName,
            buyerAddress = s.draft.buyerAddress,
            rxNo = s.draft.rxNo,
            doctor = s.draft.doctor,
            balance = s.draft.balance,
        ).getOrElse { e ->
            setState { copy(error = e.message ?: "ตรวจสอบข้อมูลไม่ผ่าน") }
            return
        }
        setState { copy(saving = true, error = null) }
        launchResult(
            block = { addKy10(form) },
            onSuccess = { setState { copy(saving = false, saved = true) } },
            onFailure = { e -> setState { copy(saving = false, error = e.message ?: ErrorMessages.SAVE_FAILED) } },
        )
    }

    fun dismissError() = setState { copy(error = null) }

    private fun patch(transform: Ky10Draft.() -> Ky10Draft) {
        setState { copy(draft = draft.transform()) }
    }
}
