package app.devper.pharm.presentation.ky

import app.devper.pharm.domain.validation.buildKy11Draft
import app.devper.pharm.domain.usecase.ky.AddKy11UseCase
import app.devper.pharm.ui.common.BaseFormViewModel

class Ky11AddViewModel(
    private val addKy11: AddKy11UseCase,
) : BaseFormViewModel<Ky11AddUiState>(Ky11AddUiState()) {

    fun onDate(v: String) = patch { copy(date = v) }
    fun onDrugName(v: String) = patch { copy(drugName = v) }
    fun onRegNo(v: String) = patch { copy(regNo = v) }
    fun onQty(v: String) = patch { copy(qty = v.filter { it.isDigit() }) }
    fun onUnit(v: String) = patch { copy(unit = v) }
    fun onBuyerName(v: String) = patch { copy(buyerName = v) }
    fun onPurpose(v: String) = patch { copy(purpose = v) }
    fun onPharmacist(v: String) = patch { copy(pharmacist = v) }

    override suspend fun persist(): Result<Unit> {
        val d = current.draft
        val form = buildKy11Draft(
            date = d.date,
            drugName = d.drugName,
            regNo = d.regNo,
            qty = d.qty,
            unit = d.unit,
            buyerName = d.buyerName,
            purpose = d.purpose,
            pharmacist = d.pharmacist,
        ).getOrElse { return Result.failure(it) }
        return addKy11(form)
    }

    private fun patch(transform: Ky11Draft.() -> Ky11Draft) {
        setState { copy(draft = draft.transform()) }
    }
}
