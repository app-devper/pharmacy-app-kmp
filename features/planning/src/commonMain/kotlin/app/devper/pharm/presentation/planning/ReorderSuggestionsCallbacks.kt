package app.devper.pharm.presentation.planning

import app.devper.pharm.domain.model.ReorderSuggestion

data class ReorderSuggestionsCallbacks(
    val onReload: () -> Unit = {},
    val onAddToPurchaseOrder: (ReorderSuggestion) -> Unit = {},
    val onAddAll: () -> Unit = {},
    val onOpenPurchaseOrder: () -> Unit = {},
    val onDismiss: (ReorderSuggestion) -> Unit = {},
    val onRowClick: (ReorderSuggestion) -> Unit = {},
    val onDismissError: () -> Unit = {},
)
