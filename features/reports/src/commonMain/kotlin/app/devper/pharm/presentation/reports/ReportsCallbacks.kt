package app.devper.pharm.presentation.reports

data class ReportsCallbacks(
    val onSelectWindow: (DashboardWindow) -> Unit = {},
    val onCloseEod: () -> Unit = {},
    val onDismissError: () -> Unit = {},
)
