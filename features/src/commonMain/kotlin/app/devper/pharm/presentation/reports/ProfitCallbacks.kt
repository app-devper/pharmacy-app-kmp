package app.devper.pharm.presentation.reports

import app.devper.pharm.presentation.reports.internal.ProfitQuickPeriod

data class ProfitCallbacks(
    val onFromMillisChange: (Long?) -> Unit = {},
    val onToMillisChange: (Long?) -> Unit = {},
    val onQuickPeriod: (ProfitQuickPeriod) -> Unit = {},
    val onSortChange: (ProfitSort) -> Unit = {},
    val onApplyRange: () -> Unit = {},
    val onExportExcel: () -> Unit = {},
    val onDismissError: () -> Unit = {},
)
