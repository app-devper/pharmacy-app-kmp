package app.devper.pharm.presentation.ky

import app.devper.pharm.domain.param.ky.ExportKyFormParam
import app.devper.pharm.domain.param.ky.KyMonthFilterParam
import app.devper.pharm.domain.usecase.ky.ExportKyFormUseCase
import app.devper.pharm.domain.usecase.ky.GetKy9EntriesUseCase
import app.devper.pharm.common.error.CommonUiStateMessage
import app.devper.pharm.presentation.ky.exception.KyUiStateError
import app.devper.pharm.ui.common.BaseLoadableViewModel

class Ky9ViewModel(
    private val getKy9Entries: GetKy9EntriesUseCase,
    private val exportKyForm: ExportKyFormUseCase,
) : BaseLoadableViewModel<Ky9UiState>(Ky9UiState()) {

    init { reload() }

    fun onMonthChange(v: String) = setState { copy(month = v) }
    fun applyFilter() = reload()

    fun exportPdf() {
        val s = current
        setState { copy(exporting = true, errorState = null, messageState = null) }
        launchResult(
            block = { exportKyForm(ExportKyFormParam(form = "ky9", month = s.month)) },
            onSuccess = { msg -> setState { copy(exporting = false, messageState = CommonUiStateMessage.ExportDone(msg)) } },
            onFailure = { e -> setState { copy(exporting = false, errorState = KyUiStateError.DownloadPdfFailed(e)) } },
        )
    }

    fun dismissMessage() = setState { copy(messageState = null) }

    fun reload() {
        val s = current
        load(
            fetch = { getKy9Entries(KyMonthFilterParam(month = s.month)) },
            failure = { e -> KyUiStateError.LoadEntriesFailed(e) },
            onLoaded = { list -> copy(entries = list) },
        )
    }
}
