package app.devper.pharm.presentation.planning

import app.devper.pharm.domain.model.ReorderSuggestion
import app.devper.pharm.ui.common.LoadableUiState

data class ReorderSuggestionsUiState(
    override val loading: Boolean = false,
    val suggestions: List<ReorderSuggestion> = emptyList(),
    override val error: String? = null,
) : LoadableUiState<ReorderSuggestionsUiState> {

    override fun withLoading(value: Boolean) = copy(loading = value)
    override fun withError(value: String?) = copy(error = value)
}
