package app.devper.pharm.presentation.ky

import app.devper.pharm.domain.model.KyFormType

data class Ky9Callbacks(
    val onSwitchForm: (KyFormType) -> Unit = {},
    val onMonthChange: (String) -> Unit = {},
    val onApply: () -> Unit = {},
    val onExport: () -> Unit = {},
    val onToggleAddForm: () -> Unit = {},
    val onDate: (String) -> Unit = {},
    val onDrugName: (String) -> Unit = {},
    val onRegNo: (String) -> Unit = {},
    val onUnit: (String) -> Unit = {},
    val onQty: (String) -> Unit = {},
    val onPricePerUnit: (String) -> Unit = {},
    val onSeller: (String) -> Unit = {},
    val onInvoiceNo: (String) -> Unit = {},
    val onSubmitAdd: () -> Unit = {},
    val onDismissMessage: () -> Unit = {},
    val onDismissError: () -> Unit = {},
)
