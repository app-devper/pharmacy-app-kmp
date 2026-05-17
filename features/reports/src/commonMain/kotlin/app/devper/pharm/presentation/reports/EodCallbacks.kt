package app.devper.pharm.presentation.reports

data class EodCallbacks(
    val onDateChange: (String) -> Unit = {},
    val onApplyDate: () -> Unit = {},
    val onRequestClose: () -> Unit = {},
    val onConfirmClose: () -> Unit = {},
    val onCancelClose: () -> Unit = {},
    val onPrint: () -> Unit = {},
    val onDismissError: () -> Unit = {},
)
