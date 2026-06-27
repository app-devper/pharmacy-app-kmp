package app.devper.pharm.presentation.ky

import app.devper.pharm.domain.model.KyFormType

data class Ky9Callbacks(
    val onSwitchForm: (KyFormType) -> Unit = {},
    val onMonthChange: (String) -> Unit = {},
    val onApply: () -> Unit = {},
    val onExport: () -> Unit = {},
    val onAddEntry: () -> Unit = {},
    val onDismissError: () -> Unit = {},
)
