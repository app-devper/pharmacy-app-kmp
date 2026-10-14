package app.devper.pharm.presentation.expiry

import app.devper.pharm.common.error.CommonUiStateError
import app.devper.pharm.common.error.CommonUiStateMessage
import app.devper.pharm.domain.param.inventory.ExpiringLotsFilterParam
import app.devper.pharm.domain.param.reports.ExportExpiringLotsCsvParam
import app.devper.pharm.domain.param.inventory.WriteoffLotsParam
import app.devper.pharm.domain.usecase.inventory.GetExpiringLotsUseCase
import app.devper.pharm.domain.usecase.inventory.WriteoffLotsUseCase
import app.devper.pharm.domain.usecase.reports.ExportExpiringLotsCsvUseCase
import app.devper.pharm.presentation.expiry.exception.ExpiryUiStateError
import app.devper.pharm.ui.common.BaseLoadableViewModel

class ExpiryViewModel(
    private val getExpiringLots: GetExpiringLotsUseCase,
    private val writeoffLots: WriteoffLotsUseCase,
    private val exportExpiringLotsCsv: ExportExpiringLotsCsvUseCase,
) : BaseLoadableViewModel<ExpiryUiState>(ExpiryUiState()) {

    init { reload() }

    fun selectWindow(window: ExpiryWindow) {
        setState { copy(window = window, selected = emptySet()) }
        reload()
    }

    fun onQueryChange(value: String) = setState { copy(query = value) }

    fun onExportExcel(headers: List<String>) {
        val s = current
        val rows = s.filteredLots
        if (rows.isEmpty()) {
            setState { copy(messageState = CommonUiStateMessage.ExportEmpty) }
            return
        }
        setState { copy(exporting = true) }
        launchResult(
            block = { exportExpiringLotsCsv(ExportExpiringLotsCsvParam(windowTag = s.window.name, rows = rows, headers = headers)) },
            onSuccess = { path -> setState { copy(exporting = false, messageState = CommonUiStateMessage.ExportDone(path)) } },
            onFailure = { e -> setState { copy(exporting = false, errorState = CommonUiStateError.ExportFailed(e)) } },
        )
    }

    fun dismissMessage() = setState { copy(messageState = null) }

    fun toggleSelected(lotId: String) = setState {
        copy(selected = if (lotId in selected) selected - lotId else selected + lotId)
    }

    fun toggleAll() = setState {
        val visibleIds = filteredLots.map { it.id }.toSet()
        copy(selected = if (allVisibleSelected) selected - visibleIds else selected + visibleIds)
    }

    fun clearSelection() = setState { copy(selected = emptySet()) }

    fun askConfirm() = setState { if (canWriteoff) copy(confirmDialog = true) else this }
    fun cancelConfirm() = setState { copy(confirmDialog = false) }

    fun confirmWriteoff() {
        val s = current
        if (s.selected.isEmpty()) return
        setState { copy(confirmDialog = false, writingOff = true, errorState = null, writeoffResult = null) }
        launchResult(
            block = { writeoffLots(WriteoffLotsParam(lotIds = s.selected.toList())) },
            onSuccess = { result ->
                setState {
                    copy(writingOff = false, writeoffResult = result, selected = emptySet())
                }
                reload()
            },
            onFailure = { e -> setState { copy(writingOff = false, errorState = ExpiryUiStateError.WriteoffFailed(e)) } },
        )
    }

    fun dismissResult() = setState { copy(writeoffResult = null) }

    fun reload() {
        val s = current
        load(
            fetch = {
                getExpiringLots(
                    ExpiringLotsFilterParam(daysAhead = s.window.daysAhead, expiredOnly = s.window.expiredOnly),
                )
            },
            failure = { e -> ExpiryUiStateError.LoadLotsFailed(e) },
            onLoaded = { lots -> copy(lots = lots, selected = selected intersect lots.map { it.id }.toSet()) },
        )
    }
}
