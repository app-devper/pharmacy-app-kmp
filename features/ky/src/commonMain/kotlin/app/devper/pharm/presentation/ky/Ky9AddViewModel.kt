package app.devper.pharm.presentation.ky

import app.devper.pharm.common.value.Money
import app.devper.pharm.common.value.Quantity

import app.devper.pharm.domain.extension.buildKy9Draft
import app.devper.pharm.domain.usecase.ky.AddKy9UseCase
import app.devper.pharm.ui.common.BaseFormViewModel

class Ky9AddViewModel(
    private val addKy9: AddKy9UseCase,
) : BaseFormViewModel<Ky9AddUiState>(Ky9AddUiState()) {

    fun onDate(v: String) = patch { copy(date = v) }
    fun onDrugName(v: String) = patch { copy(drugName = v) }
    fun onRegNo(v: String) = patch { copy(regNo = v) }
    fun onUnit(v: String) = patch { copy(unit = v) }
    fun onQty(v: String) = patch { copy(qty = v.filter { c -> c.isDigit() }) }
    fun onPricePerUnit(v: String) = patch { copy(pricePerUnit = v.numericMoney()) }
    fun onSeller(v: String) = patch { copy(seller = v) }
    fun onInvoiceNo(v: String) = patch { copy(invoiceNo = v) }

    override suspend fun persist(): Result<Unit> {
        val d = current.draft
        val param = buildKy9Draft(
            date = d.date,
            drugName = d.drugName,
            regNo = d.regNo,
            unit = d.unit,
            qty = d.qty,
            pricePerUnit = d.pricePerUnit,
            seller = d.seller,
            invoiceNo = d.invoiceNo,
        ).getOrElse { return Result.failure(it) }
        return addKy9(param)
    }

    private fun patch(transform: Ky9Draft.() -> Ky9Draft) {
        setState { copy(draft = draft.transform()) }
    }
}

private fun String.numericMoney(): String {
    val filtered = filter { it.isDigit() || it == '.' }
    val firstDot = filtered.indexOf('.')
    return if (firstDot == -1) filtered
    else filtered.substring(0, firstDot + 1) +
        filtered.substring(firstDot + 1).filter { it != '.' }
}
