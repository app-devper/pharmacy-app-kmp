package app.devper.pharm.presentation.reports

data class ProfitCallbacks(
    val onDateRangeChange: (Long?, Long?) -> Unit = { _, _ -> },
    val onSortChange: (ProfitSort) -> Unit = {},
    val onExportExcel: (List<String>) -> Unit = {},
    val onDismissError: () -> Unit = {},
)
