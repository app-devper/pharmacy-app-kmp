package app.devper.pharm.presentation.planning

import androidx.lifecycle.viewModelScope
import app.devper.pharm.domain.model.PurchaseDraftLine
import app.devper.pharm.domain.model.ReorderSuggestion
import app.devper.pharm.domain.observer.PurchaseDraftProvider
import app.devper.pharm.domain.usecase.inventory.GetReorderSuggestionsUseCase
import app.devper.pharm.presentation.planning.exception.ReorderSuggestionsUiStateError
import app.devper.pharm.ui.common.BaseLoadableViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class ReorderSuggestionsViewModel(
    private val getReorderSuggestions: GetReorderSuggestionsUseCase,
    private val purchaseDraft: PurchaseDraftProvider,
) : BaseLoadableViewModel<ReorderSuggestionsUiState>(ReorderSuggestionsUiState()) {

    init {
        reload()
        purchaseDraft.state
            .onEach { lines -> setState { copy(draftDrugIds = lines.mapTo(mutableSetOf()) { it.drugId }) } }
            .launchIn(viewModelScope)
    }

    fun addToPurchaseOrder(suggestion: ReorderSuggestion) {
        if (suggestion.drugId in current.draftDrugIds) return
        addLines(listOf(suggestion))
    }

    fun addAllToPurchaseOrder() {
        addLines(current.remainingSuggestions)
    }

    fun dismissSuggestion(suggestion: ReorderSuggestion) = setState {
        copy(suggestions = suggestions.filterNot { it.drugId == suggestion.drugId })
    }

    fun dismissMessage() = setState { copy(messageState = null) }

    private fun ReorderSuggestion.toDraftLine() = PurchaseDraftLine(
        drugId = drugId,
        drugName = drugName,
        qty = suggestedQty.value,
        costPrice = costPrice.amount,
        sellPrice = sellPrice.amount,
    )

    private fun addLines(suggestions: List<ReorderSuggestion>) {
        if (suggestions.isEmpty()) return
        val before = purchaseDraft.state.value.size
        purchaseDraft.addUnique(suggestions.map { it.toDraftLine() })
        val added = purchaseDraft.state.value.size - before
        if (added > 0) setState { copy(messageState = ReorderSuggestionsMessage.Added(added)) }
    }

    fun reload() {
        load(
            fetch = { getReorderSuggestions() },
            failure = { e -> ReorderSuggestionsUiStateError.LoadFailed(e) },
            onLoaded = { list -> copy(suggestions = list) },
        )
    }
}
