package app.devper.pharm.presentation.stockcount

data class StockCountFormCallbacks(
    val onSearchChange: (String) -> Unit = {},
    val onCountedChange: (drugId: String, value: String) -> Unit = { _, _ -> },
    val onFillFromSystem: () -> Unit = {},
    val onClear: () -> Unit = {},
    val onSave: () -> Unit = {},
    val onNotesChange: (String) -> Unit = {},
    val onBack: () -> Unit = {},
    val onDismissError: () -> Unit = {},
)
