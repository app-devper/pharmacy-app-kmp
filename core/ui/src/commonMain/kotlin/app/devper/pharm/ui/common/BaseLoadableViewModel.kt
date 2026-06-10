package app.devper.pharm.ui.common

abstract class BaseLoadableViewModel<S : LoadableUiState<S>>(initial: S) : BaseViewModel<S>(initial) {
    fun dismissError() = setState { withDomainError(null) }
}
