package app.devper.pharm.ui.common

import app.devper.pharm.common.error.CommonUiStateError
import app.devper.pharm.common.error.CommonUiStateMessage

abstract class BaseExportableViewModel<S : ExportableUiState<S>>(initial: S) : BaseLoadableViewModel<S>(initial) {

    protected fun <T> export(rows: List<T>, save: suspend (List<T>) -> Result<String>) {
        if (rows.isEmpty()) {
            setState { withMessage(CommonUiStateMessage.ExportEmpty) }
            return
        }
        setState { withExporting(true) }
        launchResult(
            block = { save(rows) },
            onSuccess = { path -> setState { withExporting(false).withMessage(CommonUiStateMessage.ExportDone(path)) } },
            onFailure = { e -> setState { withExporting(false).withDomainError(CommonUiStateError.ExportFailed(e)) } },
        )
    }

    fun dismissMessage() = setState { withMessage(null) }
}
