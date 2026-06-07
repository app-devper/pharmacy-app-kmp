package app.devper.pharm.ui.common

import app.devper.pharm.common.error.ErrorMessages
import app.devper.pharm.common.userMessageOr

abstract class BaseLoadableViewModel<S : LoadableUiState<S>>(initial: S) : BaseViewModel<S>(initial) {

    protected fun <T> launchLoad(
        block: suspend () -> Result<T>,
        fallback: String = ErrorMessages.LOAD_FAILED,
        onSuccess: S.(T) -> S,
    ) {
        setState { withLoading(true).withError(null) }
        launchResult(
            block = block,
            onSuccess = { result ->
                setState { onSuccess(result).withLoading(false).withError(null) }
            },
            onFailure = { e ->
                setState { withLoading(false).withError(e.userMessageOr(fallback)) }
            },
        )
    }

    fun dismissError() = setState { withError(null) }
}
