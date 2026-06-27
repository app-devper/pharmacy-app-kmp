package app.devper.pharm.presentation.movements

import app.devper.pharm.common.error.CommonUiStateError
import app.devper.pharm.common.error.CommonUiStateMessage
import app.devper.pharm.domain.observer.TimeZoneProvider
import app.devper.pharm.domain.param.reports.ExportMovementsCsvParam
import app.devper.pharm.domain.param.reports.MovementsFilterParam
import app.devper.pharm.domain.usecase.reports.ExportMovementsCsvUseCase
import app.devper.pharm.domain.usecase.reports.GetMovementsUseCase
import app.devper.pharm.presentation.movements.exception.MovementsUiStateError
import app.devper.pharm.ui.common.BaseLoadableViewModel
import app.devper.pharm.ui.format.DateRangeFilter

class MovementsViewModel(
    private val getMovements: GetMovementsUseCase,
    private val exportMovementsCsv: ExportMovementsCsvUseCase,
    timeZoneProvider: TimeZoneProvider,
) : BaseLoadableViewModel<MovementsUiState>(
    MovementsUiState(dateRange = DateRangeFilter(tz = timeZoneProvider.current)),
) {

    init { reload() }

    fun onFromChange(value: String) = setState { copy(dateRange = dateRange.withFrom(value), page = 1) }
    fun onToChange(value: String) = setState { copy(dateRange = dateRange.withTo(value), page = 1) }
    fun onFromMillisChange(millis: Long?) = setState {
        copy(dateRange = dateRange.withFromMillis(millis), page = 1)
    }
    fun onToMillisChange(millis: Long?) = setState {
        copy(dateRange = dateRange.withToMillis(millis), page = 1)
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

    fun onExportExcel() {
        val s = current
        if (s.items.isEmpty()) {
            setState { copy(messageState = CommonUiStateMessage.ExportEmpty) }
            return
        }
        setState { copy(exporting = true) }
        launchResult(
            block = {
                exportMovementsCsv(
                    ExportMovementsCsvParam(
                        from = s.dateRange.fromDate,
                        to = s.dateRange.toDate,
                        drugName = s.drugName,
                        rows = s.items,
                    ),
                )
            },
            onSuccess = { feedback -> setState { copy(exporting = false, messageState = CommonUiStateMessage.ExportDone(feedback)) } },
            onFailure = { e -> setState { copy(exporting = false, errorState = CommonUiStateError.ExportFailed(e)) } },
        )
    }

    fun dismissMessage() = setState { copy(messageState = null) }

    fun applyFilter() = reload()

    private fun reload() {
        val s = current
        setState { copy(loading = true, errorState = null) }
        launchResult(
            block = {
                getMovements(
                    MovementsFilterParam(
                        from = s.dateRange.fromDate,
                        to = s.dateRange.toDate,
                        drugName = s.drugName.takeIf { it.isNotBlank() },
                        types = MovementsTypeCatalog.toEnumSet(s.activeTypeIds),
                        limit = 200,
                        offset = 0,
                    ),
                )
            },
            onSuccess = { page -> setState { copy(loading = false, items = page.items, total = page.total, page = 1) } },
            onFailure = { e -> setState { copy(loading = false, errorState = MovementsUiStateError.LoadHistoryFailed(e)) } },
        )
    }
}
