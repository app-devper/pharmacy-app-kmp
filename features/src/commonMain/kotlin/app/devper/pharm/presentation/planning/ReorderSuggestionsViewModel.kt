package app.devper.pharm.presentation.planning

import app.devper.pharm.domain.usecase.GetReorderSuggestionsUseCase
import app.devper.pharm.ui.common.BaseViewModel

class ReorderSuggestionsViewModel(
    private val getReorderSuggestions: GetReorderSuggestionsUseCase,
) : BaseViewModel<ReorderSuggestionsUiState>(ReorderSuggestionsUiState()) {

    init { reload() }

    fun dismissError() = setState { copy(error = null) }

    fun reload() {
        setState { copy(loading = true, error = null) }
        launchResult(
            block = { getReorderSuggestions() },
            onSuccess = { list -> setState { copy(loading = false, suggestions = list) } },
            onFailure = { e -> setState { copy(loading = false, error = e.message ?: "โหลดข้อมูลไม่สำเร็จ") } },
        )
    }
}
