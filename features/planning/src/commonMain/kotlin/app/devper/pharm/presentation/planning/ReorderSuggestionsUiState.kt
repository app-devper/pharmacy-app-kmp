package app.devper.pharm.presentation.planning

import app.devper.pharm.common.AppException
import app.devper.pharm.domain.model.ReorderSuggestion
import app.devper.pharm.ui.common.LoadableUiState

data class ReorderSuggestionsUiState(
    override val loading: Boolean = false,
    val suggestions: List<ReorderSuggestion> = emptyList(),
    val draftDrugIds: Set<String> = emptySet(),
    val messageState: ReorderSuggestionsMessage? = null,
    val errorState: AppException? = null,
) : LoadableUiState<ReorderSuggestionsUiState> {

    override val domainError: AppException? get() = errorState
    override fun withLoading(value: Boolean) = copy(loading = value)
    override fun withDomainError(error: AppException?) = copy(errorState = error)

    val draftCount: Int get() = draftDrugIds.size
    val addedSuggestionCount: Int get() = suggestions.count { it.drugId in draftDrugIds }
    val remainingSuggestions: List<ReorderSuggestion> get() = suggestions.filterNot { it.drugId in draftDrugIds }
}

sealed interface ReorderSuggestionsMessage {
    data class Added(val count: Int) : ReorderSuggestionsMessage
}
