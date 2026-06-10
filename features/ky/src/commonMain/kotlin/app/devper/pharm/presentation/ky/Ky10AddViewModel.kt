package app.devper.pharm.presentation.ky

import app.devper.pharm.domain.validation.buildKy10Draft
import app.devper.pharm.domain.usecase.ky.AddKy10UseCase
import app.devper.pharm.ui.common.BaseFormViewModel

class Ky10AddViewModel(
    private val addKy10: AddKy10UseCase,
) : BaseFormViewModel<Ky10AddUiState>(Ky10AddUiState()) {

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

    override suspend fun persist(): Result<Unit> {
        val d = current.draft
        val form = buildKy10Draft(
            date = d.date,
            drugName = d.drugName,
            regNo = d.regNo,
            qty = d.qty,
            unit = d.unit,
            buyerName = d.buyerName,
            buyerAddress = d.buyerAddress,
            rxNo = d.rxNo,
            doctor = d.doctor,
            balance = d.balance,
        ).getOrElse { return Result.failure(it) }
        return addKy10(form)
    }

    private fun patch(transform: Ky10Draft.() -> Ky10Draft) {
        setState { copy(draft = draft.transform()) }
    }
}
