package app.devper.pharm.ui.common

import androidx.lifecycle.viewModelScope
import app.devper.pharm.common.AppException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.launch

abstract class BaseEditFormViewModel<S : EditFormUiState<S, F>, F>(initial: S) : BaseFormViewModel<S>(initial) {

    private var hydrateJob: Job? = null

    protected fun <T : Any> hydrate(
        fetch: suspend () -> T?,
        notFound: () -> AppException,
        failure: (Throwable) -> AppException,
        toForm: (T) -> F,
        extra: S.(T) -> S = { this },
    ) {
        hydrateJob?.cancel()
        setState { withLoading(true).withDomainError(null) }
        hydrateJob = viewModelScope.launch {
            val found = try {
                fetch()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Throwable) {
                setState { withLoading(false).withDomainError(failure(e)) }
                return@launch
            }
            ensureActive()
            setState {
                if (found == null) withLoading(false).withDomainError(notFound())
                else withHydrated(toForm(found)).extra(found).withLoading(false)
            }
        }
    }
}
