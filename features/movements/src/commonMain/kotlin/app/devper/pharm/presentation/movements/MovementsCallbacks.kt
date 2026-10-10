package app.devper.pharm.presentation.movements

import app.devper.pharm.ui.designsystem.PharmDateRange

data class MovementsCallbacks(
    val onSearchChange: (String) -> Unit = {},
    val onRangeChange: (PharmDateRange) -> Unit = {},
    val onApplyFilter: () -> Unit = {},
    val onToggleType: (String) -> Unit = {},
    val onPrevPage: () -> Unit = {},
    val onNextPage: () -> Unit = {},
    val onExportExcel: (List<String>) -> Unit = {},
    val onDismissError: () -> Unit = {},
)
