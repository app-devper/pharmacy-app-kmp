package app.devper.pharm.ui.common

import androidx.lifecycle.viewModelScope
import app.devper.pharm.common.AppException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.launch

abstract class BaseLoadableViewModel<S : LoadableUiState<S>>(initial: S) : BaseViewModel<S>(initial) {

    private var loadJob: Job? = null

    protected fun <T> load(
        fetch: suspend () -> Result<T>,
        failure: (Throwable) -> AppException,
        onLoaded: S.(T) -> S,
    ) {
        loadJob?.cancel()
        setState { withLoading(true).withDomainError(null) }
        loadJob = viewModelScope.launch {
            val result = try {
                fetch()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Throwable) {
                Result.failure(e)
            }
            ensureActive()
            result.fold(
                onSuccess = { value -> setState { onLoaded(value).withLoading(false) } },
                onFailure = { e -> setState { withLoading(false).withDomainError(failure(e)) } },
            )
        }
    }

    fun dismissError() = setState { withDomainError(null) }
}
