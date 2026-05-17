package app.devper.pharm.presentation.reports

import app.devper.pharm.domain.param.ReportRangeParam
import app.devper.pharm.domain.usecase.GetProfitReportUseCase
import app.devper.pharm.presentation.reports.internal.ProfitQuickPeriod
import app.devper.pharm.presentation.reports.internal.millisToYmd
import app.devper.pharm.presentation.reports.internal.resolve
import app.devper.pharm.ui.common.BaseViewModel

class ProfitViewModel(
    private val getProfitReport: GetProfitReportUseCase,
) : BaseViewModel<ProfitUiState>(ProfitUiState()) {

    init { reload() }

    fun onFromChange(v: String) = setState { copy(from = v) }
    fun onToChange(v: String) = setState { copy(to = v) }
    fun onFromMillisChange(millis: Long?) = onFromChange(millisToYmd(millis))
    fun onToMillisChange(millis: Long?) = onToChange(millisToYmd(millis))
    fun onSort(sort: ProfitSort) = setState { copy(sort = sort) }
    fun applyRange() = reload()
    fun dismissError() = setState { copy(error = null) }

    fun onQuickPeriod(period: ProfitQuickPeriod) {
        val range = period.resolve()
        setState { copy(from = millisToYmd(range.fromMillis), to = millisToYmd(range.toMillis)) }
        reload()
    }

    fun onExportExcel() {
        setState { copy(error = "Excel export ยังไม่พร้อมใช้งาน") }
    }

    fun reload() {
        val s = current
        setState { copy(loading = true, error = null) }
        launchResult(
            block = { getProfitReport(ReportRangeParam(from = s.from, to = s.to)) },
            onSuccess = { rep -> setState { copy(loading = false, report = rep) } },
            onFailure = { e -> setState { copy(loading = false, error = e.message ?: "โหลดรายงานไม่สำเร็จ") } },
        )
    }
}
