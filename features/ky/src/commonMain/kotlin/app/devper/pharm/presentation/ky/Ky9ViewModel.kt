package app.devper.pharm.presentation.ky

import app.devper.pharm.domain.param.ExportKyFormParam
import app.devper.pharm.domain.param.KyMonthFilterParam
import app.devper.pharm.domain.usecase.ExportKyFormUseCase
import app.devper.pharm.domain.usecase.GetKy9EntriesUseCase
import app.devper.pharm.ui.common.BaseViewModel

class Ky9ViewModel(
    private val getKy9Entries: GetKy9EntriesUseCase,
    private val exportKyForm: ExportKyFormUseCase,
) : BaseViewModel<Ky9UiState>(Ky9UiState()) {

    init { reload() }

    fun onMonthChange(v: String) = setState { copy(month = v) }
    fun applyFilter() = reload()

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
}
