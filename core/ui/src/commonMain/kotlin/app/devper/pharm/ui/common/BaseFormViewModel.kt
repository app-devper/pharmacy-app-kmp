package app.devper.pharm.ui.common

import app.devper.pharm.common.AppException
import app.devper.pharm.common.error.CommonUiStateError

abstract class BaseFormViewModel<S : BaseFormUiState<S>>(
    initial: S,
) : BaseViewModel<S>(initial) {

    protected open fun mapSaveError(cause: Throwable): AppException =
        cause as? AppException ?: CommonUiStateError.SaveFailed(cause)

    protected abstract suspend fun persist(): Result<Unit>

    fun submit() {
        val s = current
        if (!s.canSubmit) return
        setState { withSaving(true).withDomainError(null) }
        launchResult(
            block = { persist() },
            onSuccess = { setState { withSaving(false).withSaved(true) } },
            onFailure = { e -> setState { withSaving(false).withDomainError(mapSaveError(e)) } },
        )
    }

    fun dismissError() = setState { withDomainError(null) }
    fun resetSaved() = setState { withSaved(false) }
}
