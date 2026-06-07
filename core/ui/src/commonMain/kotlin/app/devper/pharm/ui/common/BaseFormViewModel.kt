package app.devper.pharm.ui.common

import app.devper.pharm.common.error.ErrorMessages

abstract class BaseFormViewModel<S : BaseFormUiState<S>>(
    initial: S,
) : BaseViewModel<S>(initial) {

    protected open val saveErrorFallback: String = ErrorMessages.SAVE_FAILED

    protected abstract suspend fun persist(): Result<Unit>

    fun submit() {
        val s = current
        if (!s.canSubmit) return
        setState { withSaving(true).withError(null) }
        launchResult(
            block = { persist() },
            onSuccess = { setState { withSaving(false).withSaved(true) } },
            onFailure = { e -> setState { withSaving(false).withError(e.message ?: saveErrorFallback) } },
        )
    }

    fun dismissError() = setState { withError(null) }
    fun resetSaved() = setState { withSaved(false) }
}
