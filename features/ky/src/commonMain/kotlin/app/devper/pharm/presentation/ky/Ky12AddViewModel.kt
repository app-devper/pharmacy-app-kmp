package app.devper.pharm.presentation.ky

import app.devper.pharm.domain.extension.buildKy12Draft
import app.devper.pharm.domain.usecase.AddKy12UseCase
import app.devper.pharm.ui.common.BaseFormViewModel

class Ky12AddViewModel(
    private val addKy12: AddKy12UseCase,
) : BaseFormViewModel<Ky12AddUiState>(Ky12AddUiState()) {

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

    override suspend fun persist(): Result<Unit> {
        val d = current.draft
        val form = buildKy12Draft(
            date = d.date,
            drugName = d.drugName,
            regNo = d.regNo,
            qty = d.qty,
            unit = d.unit,
            rxNo = d.rxNo,
            patientName = d.patientName,
            doctor = d.doctor,
            hospital = d.hospital,
            totalValue = d.totalValue,
            status = d.status,
        ).getOrElse { return Result.failure(it) }
        return addKy12(form)
    }

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
