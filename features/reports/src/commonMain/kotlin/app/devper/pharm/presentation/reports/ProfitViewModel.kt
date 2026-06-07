package app.devper.pharm.presentation.reports

import app.devper.pharm.common.error.ErrorMessages

import app.devper.pharm.domain.observer.TimeZoneProvider
import app.devper.pharm.domain.param.ExportProfitCsvParam
import app.devper.pharm.domain.param.ReportRangeParam
import app.devper.pharm.domain.usecase.ExportProfitCsvUseCase
import app.devper.pharm.domain.usecase.GetProfitReportUseCase
import app.devper.pharm.presentation.reports.internal.startOfMonth
import app.devper.pharm.presentation.reports.internal.todayDate
import app.devper.pharm.presentation.reports.internal.toYmd
import app.devper.pharm.ui.common.BaseViewModel
import app.devper.pharm.ui.format.DateRangeFilter

class ProfitViewModel(
    private val getProfitReport: GetProfitReportUseCase,
    private val exportProfitCsv: ExportProfitCsvUseCase,
    timeZoneProvider: TimeZoneProvider,
) : BaseViewModel<ProfitUiState>(
    ProfitUiState(
        dateRange = DateRangeFilter(
            tz = timeZoneProvider.current,
            from = todayDate(timeZoneProvider.current).startOfMonth().toYmd(),
            to = todayDate(timeZoneProvider.current).toYmd(),
        ),
    ),
) {

    init { reload() }

    fun onFromMillisChange(millis: Long?) {
        setState { copy(dateRange = dateRange.withFromMillis(millis)) }
        reload()
    }

    fun onToMillisChange(millis: Long?) {
        setState { copy(dateRange = dateRange.withToMillis(millis)) }
        reload()
    }

    fun onSort(sort: ProfitSort) = setState { copy(sort = sort) }
    fun dismissError() = setState { copy(error = null) }
    fun dismissMessage() = setState { copy(message = null) }

    fun onExportExcel() {
        val s = current
        val rows = s.sortedRows
        if (rows.isEmpty()) {
            setState { copy(message = ErrorMessages.EXPORT_EMPTY) }
            return
        }
        setState { copy(exporting = true) }
        launchResult(
            block = { exportProfitCsv(ExportProfitCsvParam(s.dateRange.fromDate, s.dateRange.toDate, rows)) },
            onSuccess = { feedback -> setState { copy(exporting = false, message = feedback) } },
            onFailure = { e -> setState { copy(exporting = false, error = e.message ?: ErrorMessages.EXPORT_FAILED) } },
        )
    }

    fun reload() {
        val s = current
        setState { copy(loading = true, error = null) }
        launchResult(
            block = { getProfitReport(ReportRangeParam(from = s.dateRange.fromDate, to = s.dateRange.toDate)) },
            onSuccess = { rep -> setState { copy(loading = false, report = rep) } },
            onFailure = { e -> setState { copy(loading = false, error = e.message ?: "โหลดรายงานไม่สำเร็จ") } },
        )
    }
}
