package app.devper.pharm.presentation.planning

import app.devper.pharm.domain.usecase.GetReorderSuggestionsUseCase
import app.devper.pharm.ui.common.BaseLoadableViewModel

class ReorderSuggestionsViewModel(
    private val getReorderSuggestions: GetReorderSuggestionsUseCase,
) : BaseLoadableViewModel<ReorderSuggestionsUiState>(ReorderSuggestionsUiState()) {

    init { reload() }

    fun reload() = launchLoad(
        block = { getReorderSuggestions() },
        onSuccess = { list -> copy(suggestions = list) },
    )
}
