package app.devper.pharm.presentation.planning

import app.devper.pharm.domain.model.ReorderSuggestion
import app.devper.pharm.ui.common.BaseUiState

data class ReorderSuggestionsUiState(
    override val loading: Boolean = false,
    val suggestions: List<ReorderSuggestion> = emptyList(),
    override val error: String? = null,
) : BaseUiState
