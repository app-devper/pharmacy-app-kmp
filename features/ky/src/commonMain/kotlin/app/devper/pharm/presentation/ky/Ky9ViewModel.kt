package app.devper.pharm.presentation.ky

import app.devper.pharm.domain.param.ExportKyFormParam
import app.devper.pharm.domain.param.KyMonthFilterParam
import app.devper.pharm.domain.usecase.ExportKyFormUseCase
import app.devper.pharm.domain.usecase.GetKy9EntriesUseCase
import app.devper.pharm.ui.common.BaseLoadableViewModel

private const val DOWNLOAD_PDF_FAILED = "ดาวน์โหลด PDF ไม่สำเร็จ"

class Ky9ViewModel(
    private val getKy9Entries: GetKy9EntriesUseCase,
    private val exportKyForm: ExportKyFormUseCase,
) : BaseLoadableViewModel<Ky9UiState>(Ky9UiState()) {

    init { reload() }

    fun onMonthChange(v: String) = setState { copy(month = v) }
    fun applyFilter() = reload()

    fun exportPdf() {
        val s = current
        setState { copy(exporting = true, error = null, message = null) }
        launchResult(
            block = { exportKyForm(ExportKyFormParam(form = "ky9", month = s.month)) },
            onSuccess = { msg -> setState { copy(exporting = false, message = msg) } },
            onFailure = { e -> setState { copy(exporting = false, error = e.message ?: DOWNLOAD_PDF_FAILED) } },
        )
    }

    fun dismissMessage() = setState { copy(message = null) }

    fun reload() {
        val s = current
        launchLoad(
            block = { getKy9Entries(KyMonthFilterParam(month = s.month)) },
            onSuccess = { list -> copy(entries = list) },
        )
    }
}
