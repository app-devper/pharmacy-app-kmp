package app.devper.pharm.presentation.customers.form

data class CustomerFormCallbacks(
    val onSubmit: () -> Unit = {},
    val onBack: () -> Unit = {},
    val onDismissError: () -> Unit = {},

    val onName: (String) -> Unit = {},
    val onPhone: (String) -> Unit = {},
    val onAllergyNote: (String) -> Unit = {},
    val onPriceTier: (String) -> Unit = {},
)
