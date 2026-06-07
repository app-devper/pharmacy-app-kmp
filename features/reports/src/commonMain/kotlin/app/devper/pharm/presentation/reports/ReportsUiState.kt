package app.devper.pharm.presentation.reports

import app.devper.pharm.domain.model.Dashboard
import app.devper.pharm.domain.model.SlowDrug
import app.devper.pharm.domain.model.TopDrug
import app.devper.pharm.ui.common.LoadableUiState

enum class DashboardWindow(val label: String, val days: Int) {
    Last7("7 วัน", 7),
    Last14("14 วัน", 14),
    Last30("30 วัน", 30),
}

data class ReportsUiState(
    val window: DashboardWindow = DashboardWindow.Last7,
    override val loading: Boolean = false,
    val dashboard: Dashboard? = null,
    val topDrugs: List<TopDrug> = emptyList(),
    val slowDrugs: List<SlowDrug> = emptyList(),
    override val error: String? = null,
) : LoadableUiState<ReportsUiState> {

    override fun withLoading(value: Boolean) = copy(loading = value)
    override fun withError(value: String?) = copy(error = value)
}
