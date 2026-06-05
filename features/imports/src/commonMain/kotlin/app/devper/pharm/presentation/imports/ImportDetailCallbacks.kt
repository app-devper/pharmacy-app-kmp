package app.devper.pharm.presentation.imports

data class ImportDetailCallbacks(
    val onBack: () -> Unit = {},
    val onEdit: (String) -> Unit = {},
    val onAskConfirm: () -> Unit = {},
    val onAskDelete: () -> Unit = {},
    val onConfirmNow: () -> Unit = {},
    val onCancelConfirm: () -> Unit = {},
    val onDeleteNow: () -> Unit = {},
    val onCancelDelete: () -> Unit = {},
    val onDismissError: () -> Unit = {},
)
