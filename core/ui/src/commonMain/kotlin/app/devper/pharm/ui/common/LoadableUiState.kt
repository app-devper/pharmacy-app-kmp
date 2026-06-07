package app.devper.pharm.ui.common

interface LoadableUiState<S : LoadableUiState<S>> : BaseUiState {
    fun withLoading(value: Boolean): S
    fun withError(value: String?): S
}
