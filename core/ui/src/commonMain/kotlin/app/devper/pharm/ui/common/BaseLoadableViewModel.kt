package app.devper.pharm.ui.common

import app.devper.pharm.common.userMessageOr

private const val DEFAULT_FALLBACK = "โหลดข้อมูลไม่สำเร็จ"

abstract class BaseLoadableViewModel<S : LoadableUiState<S>>(initial: S) : BaseViewModel<S>(initial) {

    protected fun <T> launchLoad(
        block: suspend () -> Result<T>,
        fallback: String = DEFAULT_FALLBACK,
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
