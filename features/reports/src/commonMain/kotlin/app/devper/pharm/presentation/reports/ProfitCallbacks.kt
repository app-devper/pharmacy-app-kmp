package app.devper.pharm.presentation.reports

import app.devper.pharm.ui.designsystem.PharmDateRange

data class ProfitCallbacks(
    val onRangeChange: (PharmDateRange) -> Unit = {},
    val onSortChange: (ProfitSort) -> Unit = {},
    val onExportExcel: (List<String>) -> Unit = {},
    val onDismissError: () -> Unit = {},
)
