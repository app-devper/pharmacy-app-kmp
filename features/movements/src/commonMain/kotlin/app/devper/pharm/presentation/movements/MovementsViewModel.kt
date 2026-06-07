package app.devper.pharm.presentation.movements

import app.devper.pharm.domain.param.ExportMovementsCsvParam
import app.devper.pharm.domain.param.MovementsFilterParam
import app.devper.pharm.domain.observer.TimeZoneProvider
import app.devper.pharm.domain.usecase.ExportMovementsCsvUseCase
import app.devper.pharm.domain.usecase.GetMovementsUseCase
import app.devper.pharm.presentation.movements.internal.millisToYmd
import app.devper.pharm.ui.common.BaseViewModel

class MovementsViewModel(
    private val getMovements: GetMovementsUseCase,
    private val exportMovementsCsv: ExportMovementsCsvUseCase,
    timeZoneProvider: TimeZoneProvider,
) : BaseViewModel<MovementsUiState>(MovementsUiState(tz = timeZoneProvider.current)) {

    init { reload() }

    fun onFromChange(value: String) = setState { copy(from = value, page = 1) }
    fun onToChange(value: String) = setState { copy(to = value, page = 1) }
    fun onFromMillisChange(millis: Long?) = onFromChange(millisToYmd(millis, current.tz))
    fun onToMillisChange(millis: Long?) = onToChange(millisToYmd(millis, current.tz))

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
            setState { copy(message = "ยังไม่มีข้อมูลให้ส่งออก") }
            return
        }
        setState { copy(exporting = true) }
        launchResult(
            block = {
                exportMovementsCsv(
                    ExportMovementsCsvParam(
                        from = s.from,
                        to = s.to,
                        drugName = s.drugName,
                        rows = s.items,
                    ),
                )
            },
            onSuccess = { feedback -> setState { copy(exporting = false, message = feedback) } },
            onFailure = { e -> setState { copy(exporting = false, error = e.message ?: "ส่งออกไม่สำเร็จ") } },
        )
    }

    fun dismissMessage() = setState { copy(message = null) }

    fun applyFilter() = reload()
    fun dismissError() = setState { copy(error = null) }

    private fun reload() {
        val s = current
        setState { copy(loading = true, error = null) }
        launchResult(
            block = {
                getMovements(
                    MovementsFilterParam(
                        from = s.from.takeIf { it.isNotBlank() },
                        to = s.to.takeIf { it.isNotBlank() },
                        drugName = s.drugName.takeIf { it.isNotBlank() },
                        types = MovementsTypeCatalog.toEnumSet(s.activeTypeIds),
                        limit = 200,
                        offset = 0,
                    ),
                )
            },
            onSuccess = { page ->
                setState { copy(loading = false, items = page.items, total = page.total, page = 1) }
            },
            onFailure = { e ->
                setState { copy(loading = false, error = e.message ?: "โหลดประวัติไม่สำเร็จ") }
            },
        )
    }
}
