package app.devper.pharm.presentation.ky

import app.devper.pharm.domain.extension.buildKy9Draft
import app.devper.pharm.domain.extension.isKy9DraftValid
import app.devper.pharm.domain.usecase.AddKy9UseCase
import app.devper.pharm.ui.common.BaseViewModel

class Ky9AddViewModel(
    private val addKy9: AddKy9UseCase,
) : BaseViewModel<Ky9AddUiState>(Ky9AddUiState()) {

    fun onDate(v: String) = patch { copy(date = v) }
    fun onDrugName(v: String) = patch { copy(drugName = v) }
    fun onRegNo(v: String) = patch { copy(regNo = v) }
    fun onUnit(v: String) = patch { copy(unit = v) }
    fun onQty(v: String) = patch { copy(qty = v.filter { c -> c.isDigit() }) }
    fun onPricePerUnit(v: String) = patch { copy(pricePerUnit = v.numericMoney()) }
    fun onSeller(v: String) = patch { copy(seller = v) }
    fun onInvoiceNo(v: String) = patch { copy(invoiceNo = v) }

    fun submitAdd() {
        val s = current
        if (!s.canSubmitDraft) return
        val param = buildKy9Draft(
            date = s.draft.date,
            drugName = s.draft.drugName,
            regNo = s.draft.regNo,
            unit = s.draft.unit,
            qty = s.draft.qty,
            pricePerUnit = s.draft.pricePerUnit,
            seller = s.draft.seller,
            invoiceNo = s.draft.invoiceNo,
        ).getOrElse { e ->
            setState { copy(error = e.message ?: "ตรวจสอบข้อมูลไม่ผ่าน") }
            return
        }
        setState { copy(saving = true, error = null) }
        launchResult(
            block = { addKy9(param) },
            onSuccess = { setState { copy(saving = false, saved = true) } },
            onFailure = { e -> setState { copy(saving = false, error = e.message ?: "บันทึกไม่สำเร็จ") } },
        )
    }

    fun dismissError() = setState { copy(error = null) }

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
