package app.devper.pharm.presentation.movements

data class MovementsCallbacks(
    val onSearchChange: (String) -> Unit = {},
    val onFromMillisChange: (Long?) -> Unit = {},
    val onToMillisChange: (Long?) -> Unit = {},
    val onApplyFilter: () -> Unit = {},
    val onToggleType: (String) -> Unit = {},
    val onPrevPage: () -> Unit = {},
    val onNextPage: () -> Unit = {},
    val onExportExcel: (List<String>) -> Unit = {},
    val onDismissError: () -> Unit = {},
)
