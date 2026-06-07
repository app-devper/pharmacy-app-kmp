package app.devper.pharm.presentation.expiry

import app.devper.pharm.domain.param.ExpiringLotsFilterParam
import app.devper.pharm.domain.param.WriteoffLotsParam
import app.devper.pharm.domain.usecase.GetExpiringLotsUseCase
import app.devper.pharm.domain.usecase.WriteoffLotsUseCase
import app.devper.pharm.ui.common.BaseLoadableViewModel

private const val WRITEOFF_FAILED = "ตัดจำหน่ายไม่สำเร็จ"

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
        setState { copy(confirmDialog = false, writingOff = true, error = null, writeoffResult = null) }
        launchResult(
            block = { writeoffLots(WriteoffLotsParam(lotIds = s.selected.toList())) },
            onSuccess = { result ->
                setState {
                    copy(writingOff = false, writeoffResult = result, selected = emptySet())
                }
                reload()
            },
            onFailure = { e -> setState { copy(writingOff = false, error = e.message ?: WRITEOFF_FAILED) } },
        )
    }

    fun dismissResult() = setState { copy(writeoffResult = null) }

    fun reload() {
        val s = current
        launchLoad(
            block = {
                getExpiringLots(
                    ExpiringLotsFilterParam(daysAhead = s.window.daysAhead, expiredOnly = s.window.expiredOnly),
                )
            },
            onSuccess = { lots -> copy(lots = lots) },
        )
    }
}
