package app.devper.pharm.presentation.reports

import app.devper.pharm.domain.model.DrugProfit
import app.devper.pharm.domain.model.ProfitReport
import app.devper.pharm.domain.model.ProfitSummary
import app.devper.pharm.common.AppException
import app.devper.pharm.ui.common.LoadableUiState
import app.devper.pharm.ui.format.DateRangeFilter
import app.devper.pharm.ui.i18n.PharmStrings

enum class ProfitSort {
    Profit,
    Margin,
    QtySold,
    Revenue,
}

fun ProfitSort.label(s: PharmStrings): String = when (this) {
    ProfitSort.Profit  -> s.reportsProfitHighMargin
    ProfitSort.Margin  -> "Margin %"
    ProfitSort.QtySold -> s.reportsProfitTopSelling
    ProfitSort.Revenue -> s.reportsHeaderRevenue
}

data class ProfitUiState(
    val dateRange: DateRangeFilter = DateRangeFilter(),
    val sort: ProfitSort = ProfitSort.Profit,
    override val loading: Boolean = false,
    val report: ProfitReport? = null,
    val exporting: Boolean = false,
    val message: String? = null,
    val messageState: app.devper.pharm.common.error.CommonUiStateMessage? = null,
    val errorState: AppException? = null,
) : LoadableUiState<ProfitUiState> {

    override fun withLoading(value: Boolean) = copy(loading = value)
    override val domainError: AppException? get() = errorState
    override fun withError(value: String?) = if (value == null) copy(errorState = null) else this

    val sortedRows: List<DrugProfit>
        get() {
            val rows = report?.byDrug.orEmpty()
            return when (sort) {
                ProfitSort.Profit  -> rows.sortedByDescending { it.profit }
                ProfitSort.Margin  -> rows.sortedByDescending { it.margin }
                ProfitSort.QtySold -> rows.sortedByDescending { it.qtySold }
                ProfitSort.Revenue -> rows.sortedByDescending { it.revenue }
            }
        }

    val summary: ProfitSummary? get() = report?.summary

    val missingCostCount: Int
        get() = report?.byDrug.orEmpty().count { it.revenue > 0.0 && it.cost <= 0.0 }

    val totalQty: Int
        get() = report?.byDrug.orEmpty().sumOf { it.qtySold }
}
