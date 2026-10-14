package app.devper.pharm.presentation.reports

import app.devper.pharm.ui.designsystem.PharmDateRange
import app.devper.pharm.presentation.reports.exception.ProfitUiStateError
import app.devper.pharm.domain.observer.TimeZoneProvider
import app.devper.pharm.domain.param.reports.ExportProfitCsvParam
import app.devper.pharm.domain.param.reports.ReportRangeParam
import app.devper.pharm.domain.usecase.reports.ExportProfitCsvUseCase
import app.devper.pharm.domain.usecase.reports.GetProfitReportUseCase
import app.devper.pharm.ui.format.startOfMonth
import app.devper.pharm.ui.format.todayLocalDate
import app.devper.pharm.ui.format.toYmd
import app.devper.pharm.ui.common.BaseExportableViewModel
import app.devper.pharm.ui.format.DateRangeFilter


class ProfitViewModel(
    private val getProfitReport: GetProfitReportUseCase,
    private val exportProfitCsv: ExportProfitCsvUseCase,
    timeZoneProvider: TimeZoneProvider,
) : BaseExportableViewModel<ProfitUiState>(
    ProfitUiState(
        dateRange = DateRangeFilter(
            tz = timeZoneProvider.current,
            from = todayLocalDate(timeZoneProvider.current).startOfMonth().toYmd(),
            to = todayLocalDate(timeZoneProvider.current).toYmd(),
        ),
    ),
) {

    init { reload() }

    fun onRangeChange(range: PharmDateRange) {
        val next = current.dateRange.withRange(range)
        if (next == current.dateRange) return
        setState { copy(dateRange = next) }
        reload()
    }

    fun onSort(sort: ProfitSort) = setState { copy(sort = sort) }
    fun onExportExcel(headers: List<String>) {
        val s = current
        export(s.sortedRows) { rows ->
            exportProfitCsv(
                ExportProfitCsvParam(
                    from = s.dateRange.fromDate,
                    to = s.dateRange.toDate,
                    rows = rows,
                    headers = headers,
                ),
            )
        }
    }

    fun reload() {
        val s = current
        load(
            fetch = { getProfitReport(ReportRangeParam(from = s.dateRange.fromDate, to = s.dateRange.toDate)) },
            failure = { e -> ProfitUiStateError.LoadReportFailed(e) },
            onLoaded = { rep -> copy(report = rep) },
        )
    }
}
