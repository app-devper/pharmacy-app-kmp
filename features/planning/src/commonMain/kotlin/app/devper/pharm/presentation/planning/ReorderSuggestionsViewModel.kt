package app.devper.pharm.presentation.planning

import app.devper.pharm.common.error.CommonUiStateError
import app.devper.pharm.domain.usecase.GetReorderSuggestionsUseCase
import app.devper.pharm.ui.common.BaseLoadableViewModel

class ReorderSuggestionsViewModel(
    private val getReorderSuggestions: GetReorderSuggestionsUseCase,
) : BaseLoadableViewModel<ReorderSuggestionsUiState>(ReorderSuggestionsUiState()) {

    init { reload() }

    fun reload() {
        setState { copy(loading = true, errorState = null) }
        launchResult(
            block = { getReorderSuggestions() },
            onSuccess = { list -> setState { copy(loading = false, suggestions = list) } },
            onFailure = { e -> setState { copy(loading = false, errorState = CommonUiStateError.LoadFailed(e)) } },
        )
    }
}
