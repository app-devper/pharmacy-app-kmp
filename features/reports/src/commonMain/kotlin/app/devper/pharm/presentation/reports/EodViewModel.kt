package app.devper.pharm.presentation.reports

import app.devper.pharm.common.error.CommonUiStateError
import app.devper.pharm.presentation.reports.exception.EodUiStateError

import androidx.lifecycle.viewModelScope
import app.devper.pharm.domain.model.Settings
import app.devper.pharm.domain.observer.SettingsProvider
import app.devper.pharm.domain.param.CloseEodParam
import app.devper.pharm.domain.param.EodReportParam
import app.devper.pharm.domain.usecase.reports.CloseEodUseCase
import app.devper.pharm.domain.usecase.reports.GetEodReportUseCase
import app.devper.pharm.domain.usecase.reports.PrintReceiptUseCase
import app.devper.pharm.ui.common.BaseLoadableViewModel
import app.devper.pharm.ui.format.toLocalDateOrNull
import app.devper.pharm.ui.print.buildEodReceiptTemplate
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class EodViewModel(
    settings: SettingsProvider,
    private val getEodReport: GetEodReportUseCase,
    private val closeEod: CloseEodUseCase,
    private val printReceiptUseCase: PrintReceiptUseCase,
) : BaseLoadableViewModel<EodUiState>(EodUiState()) {

    private var lastSettings: Settings = Settings()

    init {
        settings.state
            .onEach { lastSettings = it }
            .launchIn(viewModelScope)
        reload()
    }

    fun onDateChange(v: String) = setState {
        copy(date = v, closed = false, closeResult = null, closedTemplate = null)
    }

    fun applyDate() = reload()

    fun requestCloseDay() = setState { copy(confirmClose = true) }
    fun cancelCloseDay() = setState { copy(confirmClose = false) }

    fun confirmCloseDay() {
        val s = current
        setState { copy(confirmClose = false, closing = true, errorState = null) }
        launchResult(
            block = { closeEod(CloseEodParam(date = s.date.toLocalDateOrNull())) },
            onSuccess = { result ->
                val template = buildEodReceiptTemplate(closed = result, settings = lastSettings)
                setState {
                    copy(
                        closing = false,
                        closed = true,
                        closeResult = result,
                        closedTemplate = template,
                        report = result.report,
                    )
                }
            },
            onFailure = { e ->
                setState {
                    copy(
                        closing = false,
                        closed = false,
                        errorState = EodUiStateError.CloseFailed(e),
                    )
                }
            },
        )
    }

    fun printReceipt() {
        val template = current.closedTemplate
            ?: current.closeResult?.let { buildEodReceiptTemplate(closed = it, settings = lastSettings) }
            ?: return
        launchResult(
            block = { printReceiptUseCase(template) },
            onSuccess = { ok ->
                if (!ok) setState { copy(errorState = EodUiStateError.PrintReceiptUnsupported()) }
            },
            onFailure = { e -> setState { copy(errorState = EodUiStateError.PrintReceiptUnsupported(e)) } },
        )
    }

    fun reload() {
        val s = current
        setState { copy(loading = true, errorState = null) }
        launchResult(
            block = { getEodReport(EodReportParam(date = s.date.toLocalDateOrNull())) },
            onSuccess = { rep -> setState { copy(loading = false, report = rep) } },
            onFailure = { e -> setState { copy(loading = false, errorState = CommonUiStateError.LoadFailed(e)) } },
        )
    }
}
