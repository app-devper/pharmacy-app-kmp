package app.devper.pharm.presentation.planning

import androidx.lifecycle.viewModelScope
import app.devper.pharm.common.error.CommonUiStateError
import app.devper.pharm.common.error.CommonUiStateMessage
import app.devper.pharm.domain.model.PurchaseDraftLine
import app.devper.pharm.domain.model.ReorderSuggestion
import app.devper.pharm.domain.observer.PurchaseDraftProvider
import app.devper.pharm.domain.usecase.inventory.GetReorderSuggestionsUseCase
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
            .onEach { lines -> setState { copy(draftCount = lines.size) } }
            .launchIn(viewModelScope)
    }

    fun addToPurchaseOrder(suggestion: ReorderSuggestion) {
        purchaseDraft.addUnique(listOf(suggestion.toDraftLine()))
        setState { copy(messageState = CommonUiStateMessage.Saved) }
    }

    fun addAllToPurchaseOrder() {
        val lines = current.suggestions.map { it.toDraftLine() }
        if (lines.isEmpty()) return
        purchaseDraft.addUnique(lines)
        setState { copy(messageState = CommonUiStateMessage.Saved) }
    }

    fun dismissMessage() = setState { copy(messageState = null) }

    private fun ReorderSuggestion.toDraftLine() = PurchaseDraftLine(
        drugId = drugId,
        drugName = drugName,
        qty = suggestedQty.value,
        costPrice = costPrice.amount,
        sellPrice = sellPrice.amount,
    )

    fun reload() {
        setState { copy(loading = true, errorState = null) }
        launchResult(
            block = { getReorderSuggestions() },
            onSuccess = { list -> setState { copy(loading = false, suggestions = list) } },
            onFailure = { e -> setState { copy(loading = false, errorState = CommonUiStateError.LoadFailed(e)) } },
        )
    }
}
