package app.devper.pharm.presentation.expiry

data class ExpiryCallbacks(
    val onWindowChange: (ExpiryWindow) -> Unit = {},
    val onToggleRow: (String) -> Unit = {},
    val onToggleAll: () -> Unit = {},
    val onClearSelection: () -> Unit = {},
    val onAskWriteoff: () -> Unit = {},
    val onConfirmWriteoff: () -> Unit = {},
    val onCancelWriteoff: () -> Unit = {},
    val onDismissResult: () -> Unit = {},
    val onExportExcel: () -> Unit = {},
    val onDismissError: () -> Unit = {},
)
