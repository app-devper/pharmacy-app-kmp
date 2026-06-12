package app.devper.pharm.presentation.reports

import app.devper.pharm.domain.model.Dashboard
import app.devper.pharm.domain.model.SlowDrug
import app.devper.pharm.domain.model.TopDrug
import app.devper.pharm.common.AppException
import app.devper.pharm.ui.common.LoadableUiState

enum class DashboardWindow(val days: Int) {
    Last7(7),
    Last14(14),
    Last30(30),
}

fun DashboardWindow.label(s: app.devper.pharm.ui.i18n.PharmStrings): String = when (this) {
    DashboardWindow.Last7 -> s.planningDaysLeftLabel(7)
    DashboardWindow.Last14 -> s.planningDaysLeftLabel(14)
    DashboardWindow.Last30 -> s.planningDaysLeftLabel(30)
}

data class ReportsUiState(
    val window: DashboardWindow = DashboardWindow.Last7,
    override val loading: Boolean = false,
    val dashboard: Dashboard? = null,
    val monthProfit: Double? = null,
    val topDrugs: List<TopDrug> = emptyList(),
    val slowDrugs: List<SlowDrug> = emptyList(),
    val errorState: AppException? = null,
) : LoadableUiState<ReportsUiState> {

    override fun withLoading(value: Boolean) = copy(loading = value)
    override val domainError: AppException? get() = errorState
    override fun withDomainError(error: AppException?) = copy(errorState = error)
}
