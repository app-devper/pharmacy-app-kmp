package app.devper.pharm.presentation.ky

import app.devper.pharm.domain.repository.export.ExportRepository
import app.devper.pharm.domain.repository.ky.KyRepository
import app.devper.pharm.domain.model.KyFormType
import app.devper.pharm.domain.param.ky.ExportKyFormParam
import app.devper.pharm.domain.param.ky.KyMonthFilterParam
import app.devper.pharm.common.error.CommonUiStateMessage
import app.devper.pharm.presentation.ky.exception.KyUiStateError
import app.devper.pharm.ui.common.BaseLoadableViewModel

class KyListViewModel(
    private val ky: KyRepository,
    private val export: ExportRepository,
) : BaseLoadableViewModel<KyListUiState>(KyListUiState()) {

    fun init(formType: KyFormType) {
        setState { copy(formType = formType, rows = emptyList()) }
        reload()
    }

    fun onMonthChange(v: String) = setState { copy(month = v) }
    fun applyFilter() = reload()
    fun dismissMessage() = setState { copy(messageState = null) }

    fun exportPdf() {
        val s = current
        setState { copy(exporting = true, errorState = null, messageState = null) }
        launchResult(
            block = { Result.success(export.exportKyForm(ExportKyFormParam(form = s.formType.wire, month = s.month))) },
            onSuccess = { msg -> setState { copy(exporting = false, messageState = CommonUiStateMessage.ExportDone(msg)) } },
            onFailure = { e -> setState { copy(exporting = false, errorState = KyUiStateError.DownloadPdfFailed(e)) } },
        )
    }

    fun reload() {
        val s = current
        val filter = KyMonthFilterParam(month = s.month)
        load(
            fetch = {
                when (s.formType) {
                    KyFormType.Ky10 -> Result.success(ky.listKy10(filter).map(KyRow::Ky10))
                    KyFormType.Ky11 -> Result.success(ky.listKy11(filter).map(KyRow::Ky11))
                    KyFormType.Ky12 -> Result.success(ky.listKy12(filter).map(KyRow::Ky12))
                    KyFormType.Ky9  -> Result.success(emptyList())
                }
            },
            failure = { e -> KyUiStateError.LoadEntriesFailed(e) },
            onLoaded = { rows -> copy(rows = rows) },
        )
    }
}
