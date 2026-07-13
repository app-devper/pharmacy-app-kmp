package app.devper.pharm.presentation.planning

import app.devper.pharm.common.AppException
import app.devper.pharm.common.error.CommonUiStateMessage
import app.devper.pharm.domain.model.ReorderSuggestion
import app.devper.pharm.ui.common.LoadableUiState

data class ReorderSuggestionsUiState(
    override val loading: Boolean = false,
    val suggestions: List<ReorderSuggestion> = emptyList(),
    val draftCount: Int = 0,
    val messageState: CommonUiStateMessage? = null,
    val errorState: AppException? = null,
) : LoadableUiState<ReorderSuggestionsUiState> {

    override val domainError: AppException? get() = errorState
    override fun withLoading(value: Boolean) = copy(loading = value)
    override fun withDomainError(error: AppException?) = copy(errorState = error)
}
