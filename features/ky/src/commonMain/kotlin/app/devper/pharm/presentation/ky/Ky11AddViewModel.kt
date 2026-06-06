package app.devper.pharm.presentation.ky

import app.devper.pharm.domain.parser.Ky11DraftBuilder
import app.devper.pharm.domain.usecase.AddKy11UseCase
import app.devper.pharm.ui.common.BaseViewModel

class Ky11AddViewModel(
    private val addKy11: AddKy11UseCase,
) : BaseViewModel<Ky11AddUiState>(Ky11AddUiState()) {

    fun onDate(v: String) = patch { copy(date = v) }
    fun onDrugName(v: String) = patch { copy(drugName = v) }
    fun onRegNo(v: String) = patch { copy(regNo = v) }
    fun onQty(v: String) = patch { copy(qty = v.filter { it.isDigit() }) }
    fun onUnit(v: String) = patch { copy(unit = v) }
    fun onBuyerName(v: String) = patch { copy(buyerName = v) }
    fun onPurpose(v: String) = patch { copy(purpose = v) }
    fun onPharmacist(v: String) = patch { copy(pharmacist = v) }

    fun submitAdd() {
        val s = current
        if (!s.canSubmitDraft) return
        val form = Ky11DraftBuilder.build(
            date = s.draft.date,
            drugName = s.draft.drugName,
            regNo = s.draft.regNo,
            qty = s.draft.qty,
            unit = s.draft.unit,
            buyerName = s.draft.buyerName,
            purpose = s.draft.purpose,
            pharmacist = s.draft.pharmacist,
        ).getOrElse { e ->
            setState { copy(error = e.message ?: "ตรวจสอบข้อมูลไม่ผ่าน") }
            return
        }
        setState { copy(saving = true, error = null) }
        launchResult(
            block = { addKy11(form) },
            onSuccess = { setState { copy(saving = false, saved = true) } },
            onFailure = { e -> setState { copy(saving = false, error = e.message ?: "บันทึกไม่สำเร็จ") } },
        )
    }

    fun dismissError() = setState { copy(error = null) }

    private fun patch(transform: Ky11Draft.() -> Ky11Draft) {
        setState { copy(draft = draft.transform()) }
    }
}
