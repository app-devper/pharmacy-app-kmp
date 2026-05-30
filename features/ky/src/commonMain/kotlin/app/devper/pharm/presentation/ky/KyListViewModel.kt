package app.devper.pharm.presentation.ky

import app.devper.pharm.domain.model.KyFormType
import app.devper.pharm.domain.param.ExportKyFormParam
import app.devper.pharm.domain.param.KyMonthFilterParam
import app.devper.pharm.domain.usecase.ExportKyFormUseCase
import app.devper.pharm.domain.usecase.GetKy10EntriesUseCase
import app.devper.pharm.domain.usecase.GetKy11EntriesUseCase
import app.devper.pharm.domain.usecase.GetKy12EntriesUseCase
import app.devper.pharm.ui.common.BaseViewModel

class KyListViewModel(
    private val getKy10: GetKy10EntriesUseCase,
    private val getKy11: GetKy11EntriesUseCase,
    private val getKy12: GetKy12EntriesUseCase,
    private val exportKyForm: ExportKyFormUseCase,
) : BaseViewModel<KyListUiState>(KyListUiState()) {

    fun init(formType: KyFormType) {
        setState { copy(formType = formType, rows = emptyList()) }
        reload()
    }

    fun onMonthChange(v: String) = setState { copy(month = v) }
    fun applyFilter() = reload()
    fun dismissMessage() = setState { copy(message = null) }
    fun dismissError() = setState { copy(error = null) }

    fun exportPdf() {
        val s = current
        setState { copy(exporting = true, error = null, message = null) }
        launchResult(
            block = { exportKyForm(ExportKyFormParam(form = s.formType.wire, month = s.month)) },
            onSuccess = { msg -> setState { copy(exporting = false, message = msg) } },
            onFailure = { e -> setState { copy(exporting = false, error = e.message ?: "ดาวน์โหลด PDF ไม่สำเร็จ") } },
        )
    }

    fun reload() {
        val s = current
        val filter = KyMonthFilterParam(month = s.month)
        setState { copy(loading = true, error = null) }
        launchResult(
            block = {
                when (s.formType) {
                    KyFormType.Ky10 -> getKy10(filter).map { list -> list.map(KyRow::Ky10) }
                    KyFormType.Ky11 -> getKy11(filter).map { list -> list.map(KyRow::Ky11) }
                    KyFormType.Ky12 -> getKy12(filter).map { list -> list.map(KyRow::Ky12) }
                    KyFormType.Ky9  -> Result.success(emptyList())
                }
            },
            onSuccess = { rows -> setState { copy(loading = false, rows = rows) } },
            onFailure = { e -> setState { copy(loading = false, error = e.message ?: "โหลดข้อมูลไม่สำเร็จ") } },
        )
    }
}
