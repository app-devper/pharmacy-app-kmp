package app.devper.pharm.ui.common

interface EditFormUiState<S : EditFormUiState<S, F>, F> : BaseFormUiState<S> {
    fun withLoading(loading: Boolean): S
    fun withHydrated(form: F): S
}
