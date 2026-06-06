package app.devper.pharm.presentation.reports

data class ProfitCallbacks(
    val onFromMillisChange: (Long?) -> Unit = {},
    val onToMillisChange: (Long?) -> Unit = {},
    val onSortChange: (ProfitSort) -> Unit = {},
    val onExportExcel: () -> Unit = {},
    val onDismissError: () -> Unit = {},
)
