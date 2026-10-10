package app.devper.pharm.presentation.saleshistory

import app.devper.pharm.ui.format.QuickPeriod
import app.devper.pharm.ui.designsystem.PharmDateRange
import app.devper.pharm.domain.model.SaleSummary

data class SalesHistoryCallbacks(
    val onQueryChange: (String) -> Unit = {},
    val onRangeChange: (PharmDateRange) -> Unit = {},
    val onApplyFilter: () -> Unit = {},
    val onSelectPeriod: (QuickPeriod) -> Unit = {},
    val onOpenReceipt: (SaleSummary) -> Unit = {},
    val onStartReturn: (SaleSummary) -> Unit = {},
    val onDismissError: () -> Unit = {},
)
