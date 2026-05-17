package app.devper.pharm.presentation.ky

import app.devper.pharm.domain.param.ExportKyFormParam
import app.devper.pharm.domain.param.KyMonthFilterParam
import app.devper.pharm.domain.parser.Ky9DraftBuilder
import app.devper.pharm.domain.usecase.AddKy9UseCase
import app.devper.pharm.domain.usecase.ExportKyFormUseCase
import app.devper.pharm.domain.usecase.GetKy9EntriesUseCase
import app.devper.pharm.ui.common.BaseViewModel

class Ky9ViewModel(
    private val getKy9Entries: GetKy9EntriesUseCase,
    private val addKy9: AddKy9UseCase,
    private val exportKyForm: ExportKyFormUseCase,
) : BaseViewModel<Ky9UiState>(Ky9UiState()) {

    init { reload() }

    fun onMonthChange(v: String) = setState { copy(month = v) }
    fun applyFilter() = reload()

    fun toggleAddForm() = setState { copy(addFormOpen = !addFormOpen, draft = Ky9Draft()) }

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
        val param = Ky9DraftBuilder.build(
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
            onSuccess = {
                setState { copy(saving = false, addFormOpen = false, draft = Ky9Draft()) }
                reload()
            },
            onFailure = { e -> setState { copy(saving = false, error = e.message ?: "บันทึกไม่สำเร็จ") } },
        )
    }

    fun exportPdf() {
        val s = current
        setState { copy(exporting = true, error = null, message = null) }
        launchResult(
            block = { exportKyForm(ExportKyFormParam(form = "ky9", month = s.month)) },
            onSuccess = { msg -> setState { copy(exporting = false, message = msg) } },
            onFailure = { e -> setState { copy(exporting = false, error = e.message ?: "ดาวน์โหลด PDF ไม่สำเร็จ") } },
        )
    }

    fun dismissMessage() = setState { copy(message = null) }
    fun dismissError() = setState { copy(error = null) }

    fun reload() {
        val s = current
        setState { copy(loading = true, error = null) }
        launchResult(
            block = { getKy9Entries(KyMonthFilterParam(month = s.month)) },
            onSuccess = { list -> setState { copy(loading = false, entries = list) } },
            onFailure = { e -> setState { copy(loading = false, error = e.message ?: "โหลดข้อมูลไม่สำเร็จ") } },
        )
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
