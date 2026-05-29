package app.devper.pharm.ui.common

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

abstract class BaseViewModel<S : BaseUiState>(
    initial: S,
) : ViewModel() {

    private val _state = MutableStateFlow(initial)
    val state: StateFlow<S> = _state.asStateFlow()

    protected val current: S get() = _state.value

    protected fun setState(transform: S.() -> S) {
        _state.update { it.transform() }
    }

    protected fun <T> launchResult(
        block: suspend () -> Result<T>,
        onSuccess: suspend (T) -> Unit,
        onFailure: suspend (Throwable) -> Unit = {  },
        withLoading: ((Boolean) -> Unit)? = null,
    ) {
        withLoading?.invoke(true)
        viewModelScope.launch {
            try {
                block().fold(
                    onSuccess = { value -> onSuccess(value) },
                    onFailure = { e -> onFailure(e) },
                )
            } finally {
                withLoading?.invoke(false)
            }
        }
    }
}
