package app.devper.pharm.presentation.stockcount

data class StockCountFormCallbacks(
    val onSearchChange: (String) -> Unit = {},
    val onCountedChange: (drugId: String, value: String) -> Unit = { _, _ -> },
    val onFillFromSystem: () -> Unit = {},
    val onClearDraft: () -> Unit = {},
    val onConfirmDraftAction: () -> Unit = {},
    val onCancelDraftAction: () -> Unit = {},
    val onSave: () -> Unit = {},
    val onConfirmSubmit: () -> Unit = {},
    val onCancelSubmit: () -> Unit = {},
    val onNotesChange: (String) -> Unit = {},
    val onBack: () -> Unit = {},
    val onDismissError: () -> Unit = {},
)
