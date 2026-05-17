package app.devper.pharm.presentation.reports

import app.devper.pharm.domain.param.EodReportParam
import app.devper.pharm.domain.usecase.GetEodReportUseCase
import app.devper.pharm.ui.common.BaseViewModel

class EodViewModel(
    private val getEodReport: GetEodReportUseCase,
) : BaseViewModel<EodUiState>(EodUiState()) {

    init { reload() }

    fun onDateChange(v: String) = setState { copy(date = v, closed = false) }
    fun applyDate() = reload()
    fun dismissError() = setState { copy(error = null) }

    fun requestCloseDay() = setState { copy(confirmClose = true) }
    fun cancelCloseDay() = setState { copy(confirmClose = false) }
    fun confirmCloseDay() = setState { copy(confirmClose = false, closed = true) }
    fun printReceipt() = Unit

    fun reload() {
        val s = current
        setState { copy(loading = true, error = null) }
        launchResult(
            block = { getEodReport(EodReportParam(date = s.date)) },
            onSuccess = { rep -> setState { copy(loading = false, report = rep) } },
            onFailure = { e -> setState { copy(loading = false, error = e.message ?: "โหลดรายงานไม่สำเร็จ") } },
        )
    }
}
