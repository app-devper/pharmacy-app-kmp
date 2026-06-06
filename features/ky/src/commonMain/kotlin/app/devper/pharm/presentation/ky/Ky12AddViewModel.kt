package app.devper.pharm.presentation.ky

import app.devper.pharm.domain.parser.Ky12DraftBuilder
import app.devper.pharm.domain.usecase.AddKy12UseCase
import app.devper.pharm.ui.common.BaseViewModel

class Ky12AddViewModel(
    private val addKy12: AddKy12UseCase,
) : BaseViewModel<Ky12AddUiState>(Ky12AddUiState()) {

    fun onDate(v: String) = patch { copy(date = v) }
    fun onDrugName(v: String) = patch { copy(drugName = v) }
    fun onRegNo(v: String) = patch { copy(regNo = v) }
    fun onQty(v: String) = patch { copy(qty = v.filter { it.isDigit() }) }
    fun onUnit(v: String) = patch { copy(unit = v) }
    fun onRxNo(v: String) = patch { copy(rxNo = v) }
    fun onPatientName(v: String) = patch { copy(patientName = v) }
    fun onDoctor(v: String) = patch { copy(doctor = v) }
    fun onHospital(v: String) = patch { copy(hospital = v) }
    fun onTotalValue(v: String) = patch { copy(totalValue = v.numericMoneyKy12()) }
    fun onStatus(v: String) = patch { copy(status = v) }

    fun submitAdd() {
        val s = current
        if (!s.canSubmitDraft) return
        val form = Ky12DraftBuilder.build(
            date = s.draft.date,
            drugName = s.draft.drugName,
            regNo = s.draft.regNo,
            qty = s.draft.qty,
            unit = s.draft.unit,
            rxNo = s.draft.rxNo,
            patientName = s.draft.patientName,
            doctor = s.draft.doctor,
            hospital = s.draft.hospital,
            totalValue = s.draft.totalValue,
            status = s.draft.status,
        ).getOrElse { e ->
            setState { copy(error = e.message ?: "ตรวจสอบข้อมูลไม่ผ่าน") }
            return
        }
        setState { copy(saving = true, error = null) }
        launchResult(
            block = { addKy12(form) },
            onSuccess = { setState { copy(saving = false, saved = true) } },
            onFailure = { e -> setState { copy(saving = false, error = e.message ?: "บันทึกไม่สำเร็จ") } },
        )
    }

    fun dismissError() = setState { copy(error = null) }

    private fun patch(transform: Ky12Draft.() -> Ky12Draft) {
        setState { copy(draft = draft.transform()) }
    }
}

private fun String.numericMoneyKy12(): String {
    val filtered = filter { it.isDigit() || it == '.' }
    val firstDot = filtered.indexOf('.')
    return if (firstDot == -1) filtered
    else filtered.substring(0, firstDot + 1) +
        filtered.substring(firstDot + 1).filter { it != '.' }
}
