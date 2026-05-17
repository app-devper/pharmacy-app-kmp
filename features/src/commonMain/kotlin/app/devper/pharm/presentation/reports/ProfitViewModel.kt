package app.devper.pharm.presentation.reports

import app.devper.pharm.domain.param.ExportProfitCsvParam
import app.devper.pharm.domain.param.ReportRangeParam
import app.devper.pharm.domain.usecase.ExportProfitCsvUseCase
import app.devper.pharm.domain.usecase.GetProfitReportUseCase
import app.devper.pharm.presentation.reports.internal.ProfitQuickPeriod
import app.devper.pharm.presentation.reports.internal.millisToYmd
import app.devper.pharm.presentation.reports.internal.resolve
import app.devper.pharm.ui.common.BaseViewModel

class ProfitViewModel(
    private val getProfitReport: GetProfitReportUseCase,
    private val exportProfitCsv: ExportProfitCsvUseCase,
) : BaseViewModel<ProfitUiState>(ProfitUiState()) {

    init { reload() }

    fun onFromChange(v: String) = setState { copy(from = v) }
    fun onToChange(v: String) = setState { copy(to = v) }
    fun onFromMillisChange(millis: Long?) = onFromChange(millisToYmd(millis))
    fun onToMillisChange(millis: Long?) = onToChange(millisToYmd(millis))
    fun onSort(sort: ProfitSort) = setState { copy(sort = sort) }
    fun applyRange() = reload()
    fun dismissError() = setState { copy(error = null) }
    fun dismissMessage() = setState { copy(message = null) }

    fun onQuickPeriod(period: ProfitQuickPeriod) {
        val range = period.resolve()
        setState { copy(from = millisToYmd(range.fromMillis), to = millisToYmd(range.toMillis)) }
        reload()
    }

    fun onExportExcel() {
        val s = current
        val rows = s.sortedRows
        if (rows.isEmpty()) {
            setState { copy(message = "ยังไม่มีข้อมูลให้ส่งออก") }
            return
        }
        setState { copy(exporting = true) }
        launchResult(
            block = { exportProfitCsv(ExportProfitCsvParam(s.from, s.to, rows)) },
            onSuccess = { feedback -> setState { copy(exporting = false, message = feedback) } },
            onFailure = { e -> setState { copy(exporting = false, error = e.message ?: "ส่งออกไม่สำเร็จ") } },
        )
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
