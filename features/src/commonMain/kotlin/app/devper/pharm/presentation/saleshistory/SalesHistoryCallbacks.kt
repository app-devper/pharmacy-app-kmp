package app.devper.pharm.presentation.saleshistory

import app.devper.pharm.domain.model.SaleSummary

data class SalesHistoryCallbacks(
    val onQueryChange: (String) -> Unit = {},
    val onFromMillisChange: (Long?) -> Unit = {},
    val onToMillisChange: (Long?) -> Unit = {},
    val onApplyFilter: () -> Unit = {},
    val onOpenReceipt: (SaleSummary) -> Unit = {},
    val onStartReturn: (SaleSummary) -> Unit = {},
    val onDismissError: () -> Unit = {},
)
