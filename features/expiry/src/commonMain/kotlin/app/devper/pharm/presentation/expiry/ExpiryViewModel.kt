package app.devper.pharm.presentation.expiry

import app.devper.pharm.common.error.CommonUiStateError
import app.devper.pharm.domain.param.inventory.ExpiringLotsFilterParam
import app.devper.pharm.domain.param.inventory.WriteoffLotsParam
import app.devper.pharm.domain.usecase.inventory.GetExpiringLotsUseCase
import app.devper.pharm.domain.usecase.inventory.WriteoffLotsUseCase
import app.devper.pharm.presentation.expiry.exception.ExpiryUiStateError
import app.devper.pharm.ui.common.BaseLoadableViewModel

class ExpiryViewModel(
    private val getExpiringLots: GetExpiringLotsUseCase,
    private val writeoffLots: WriteoffLotsUseCase,
) : BaseLoadableViewModel<ExpiryUiState>(ExpiryUiState()) {

    init { reload() }

    fun selectWindow(window: ExpiryWindow) {
        setState { copy(window = window, selected = emptySet()) }
        reload()
    }

    fun toggleSelected(lotId: String) = setState {
        copy(selected = if (lotId in selected) selected - lotId else selected + lotId)
    }

    fun toggleAll() = setState {
        copy(selected = if (allSelected) emptySet() else lots.map { it.id }.toSet())
    }

    fun selectAll() = setState { copy(selected = lots.map { it.id }.toSet()) }
    fun clearSelection() = setState { copy(selected = emptySet()) }

    fun askConfirm() = setState { copy(confirmDialog = true) }
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
        setState { copy(loading = true, errorState = null) }
        launchResult(
            block = {
                getExpiringLots(
                    ExpiringLotsFilterParam(daysAhead = s.window.daysAhead, expiredOnly = s.window.expiredOnly),
                )
            },
            onSuccess = { lots -> setState { copy(loading = false, lots = lots) } },
            onFailure = { e -> setState { copy(loading = false, errorState = CommonUiStateError.LoadFailed(e)) } },
        )
    }
}
