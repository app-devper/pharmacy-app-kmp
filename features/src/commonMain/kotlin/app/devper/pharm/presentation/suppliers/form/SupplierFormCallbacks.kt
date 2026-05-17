package app.devper.pharm.presentation.suppliers.form

data class SupplierFormCallbacks(
    val onSubmit: () -> Unit = {},
    val onBack: () -> Unit = {},
    val onDismissError: () -> Unit = {},

    val onName: (String) -> Unit = {},
    val onContactName: (String) -> Unit = {},
    val onPhone: (String) -> Unit = {},
    val onAddress: (String) -> Unit = {},
    val onTaxId: (String) -> Unit = {},
    val onNotes: (String) -> Unit = {},
)
