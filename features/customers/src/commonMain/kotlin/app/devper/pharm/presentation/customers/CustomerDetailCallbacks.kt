package app.devper.pharm.presentation.customers

data class CustomerDetailCallbacks(
    val onBack: () -> Unit = {},
    val onEdit: () -> Unit = {},
    val onDismissError: () -> Unit = {},
)
