package app.devper.pharm.presentation.reports

import app.devper.pharm.common.error.CommonUiStateError
import app.devper.pharm.common.error.CommonUiStateMessage
import app.devper.pharm.presentation.reports.exception.ProfitUiStateError
import app.devper.pharm.domain.observer.TimeZoneProvider
import app.devper.pharm.domain.param.reports.ExportProfitCsvParam
import app.devper.pharm.domain.param.reports.ReportRangeParam
import app.devper.pharm.domain.usecase.reports.ExportProfitCsvUseCase
import app.devper.pharm.domain.usecase.reports.GetProfitReportUseCase
import app.devper.pharm.presentation.reports.internal.startOfMonth
import app.devper.pharm.presentation.reports.internal.todayDate
import app.devper.pharm.presentation.reports.internal.toYmd
import app.devper.pharm.ui.common.BaseLoadableViewModel
import app.devper.pharm.ui.format.DateRangeFilter


class ProfitViewModel(
    private val getProfitReport: GetProfitReportUseCase,
    private val exportProfitCsv: ExportProfitCsvUseCase,
    timeZoneProvider: TimeZoneProvider,
) : BaseLoadableViewModel<ProfitUiState>(
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
    fun dismissMessage() = setState { copy(messageState = null) }

    fun onExportExcel() {
        val s = current
        val rows = s.sortedRows
        if (rows.isEmpty()) {
            setState { copy(messageState = CommonUiStateMessage.ExportEmpty) }
            return
        }
        setState { copy(exporting = true) }
        launchResult(
            block = { exportProfitCsv(ExportProfitCsvParam(s.dateRange.fromDate, s.dateRange.toDate, rows)) },
            onSuccess = { feedback -> setState { copy(exporting = false, messageState = CommonUiStateMessage.ExportDone(feedback)) } },
            onFailure = { e -> setState { copy(exporting = false, errorState = CommonUiStateError.ExportFailed(e)) } },
        )
    }

    fun reload() {
        val s = current
        setState { copy(loading = true, errorState = null) }
        launchResult(
            block = { getProfitReport(ReportRangeParam(from = s.dateRange.fromDate, to = s.dateRange.toDate)) },
            onSuccess = { rep -> setState { copy(loading = false, report = rep) } },
            onFailure = { e -> setState { copy(loading = false, errorState = ProfitUiStateError.LoadReportFailed(e)) } },
        )
    }
}
