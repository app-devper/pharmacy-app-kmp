package app.devper.pharm.presentation.reports

import app.devper.pharm.domain.param.ExportProfitCsvParam
import app.devper.pharm.domain.param.ReportRangeParam
import app.devper.pharm.domain.usecase.ExportProfitCsvUseCase
import app.devper.pharm.domain.usecase.GetProfitReportUseCase
import app.devper.pharm.presentation.reports.internal.millisToYmd
import app.devper.pharm.presentation.reports.internal.startOfMonth
import app.devper.pharm.presentation.reports.internal.todayDate
import app.devper.pharm.presentation.reports.internal.toYmd
import app.devper.pharm.ui.common.BaseViewModel

class ProfitViewModel(
    private val getProfitReport: GetProfitReportUseCase,
    private val exportProfitCsv: ExportProfitCsvUseCase,
) : BaseViewModel<ProfitUiState>(
    ProfitUiState(from = todayDate().startOfMonth().toYmd(), to = todayDate().toYmd()),
) {

    init { reload() }

    fun onFromMillisChange(millis: Long?) {
        setState { copy(from = millisToYmd(millis)) }
        reload()
    }

    fun onToMillisChange(millis: Long?) {
        setState { copy(to = millisToYmd(millis)) }
        reload()
    }

    fun onSort(sort: ProfitSort) = setState { copy(sort = sort) }
    fun dismissError() = setState { copy(error = null) }
    fun dismissMessage() = setState { copy(message = null) }

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
