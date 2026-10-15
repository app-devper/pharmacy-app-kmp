package app.devper.pharm.presentation.movements

import app.devper.pharm.ui.designsystem.PharmDateRange
import app.devper.pharm.domain.observer.TimeZoneProvider
import app.devper.pharm.domain.param.reports.ExportMovementsCsvParam
import app.devper.pharm.domain.param.reports.MovementsFilterParam
import app.devper.pharm.domain.usecase.reports.ExportMovementsCsvUseCase
import app.devper.pharm.domain.repository.reports.MovementsRepository
import app.devper.pharm.presentation.movements.exception.MovementsUiStateError
import app.devper.pharm.ui.common.BaseExportableViewModel
import app.devper.pharm.ui.format.DateRangeFilter

class MovementsViewModel(
    private val movements: MovementsRepository,
    private val exportMovementsCsv: ExportMovementsCsvUseCase,
    timeZoneProvider: TimeZoneProvider,
) : BaseExportableViewModel<MovementsUiState>(
    MovementsUiState(dateRange = DateRangeFilter(tz = timeZoneProvider.current)),
) {

    init { reload() }

    fun onRangeChange(range: PharmDateRange) = setState {
        copy(dateRange = dateRange.withRange(range), page = 1)
    }

    fun onSearchChange(value: String) = setState { copy(drugName = value, page = 1) }

    fun onToggleType(id: String) = setState {
        val next = if (id in activeTypeIds) activeTypeIds - id else activeTypeIds + id
        copy(activeTypeIds = next, page = 1)
    }

    fun onPrevPage() = setState {
        copy(page = (page - 1).coerceAtLeast(1))
    }

    fun onNextPage() = setState {
        val max = pageCount
        copy(page = (page + 1).coerceAtMost(max))
    }

    fun onExportExcel(headers: List<String>) {
        val s = current
        export(s.items) { rows ->
            exportMovementsCsv(
                ExportMovementsCsvParam(
                    from = s.dateRange.fromDate,
                    to = s.dateRange.toDate,
                    drugName = s.drugName,
                    rows = rows,
                    headers = headers,
                ),
            )
        }
    }

    fun applyFilter() = reload()

    private fun reload() {
        val s = current
        load(
            fetch = {
                Result.success(
                    movements.list(
                        MovementsFilterParam(
                            from = s.dateRange.fromDate,
                            to = s.dateRange.toDate,
                            drugName = s.drugName.takeIf { it.isNotBlank() },
                            types = MovementsTypeCatalog.toEnumSet(s.activeTypeIds),
                            limit = 200,
                            offset = 0,
                        ),
                    ),
                )
            },
            failure = { e -> MovementsUiStateError.LoadHistoryFailed(e) },
            onLoaded = { page -> copy(items = page.items, total = page.total, page = 1) },
        )
    }
}
