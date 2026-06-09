package app.devper.pharm.ui.common

import app.devper.pharm.common.AppException
import app.devper.pharm.common.error.CommonUiStateError
import app.devper.pharm.common.error.ErrorMessages
import app.devper.pharm.common.userMessageOr

abstract class BaseFormViewModel<S : BaseFormUiState<S>>(
    initial: S,
) : BaseViewModel<S>(initial) {

    protected open val saveErrorFallback: String = ErrorMessages.SAVE_FAILED

    protected open fun mapSaveError(cause: Throwable): AppException =
        cause as? AppException ?: CommonUiStateError.SaveFailed(cause)

    protected abstract suspend fun persist(): Result<Unit>

    fun submit() {
        val s = current
        if (!s.canSubmit) return
        setState { withSaving(true).withError(null).withDomainError(null) }
        launchResult(
            block = { persist() },
            onSuccess = { setState { withSaving(false).withSaved(true) } },
            onFailure = { e ->
                setState {
                    withSaving(false)
                        .withError(e.userMessageOr(saveErrorFallback))
                        .withDomainError(mapSaveError(e))
                }
            },
        )
    }

    fun dismissError() = setState { withError(null).withDomainError(null) }
    fun resetSaved() = setState { withSaved(false) }
}
