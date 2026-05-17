package app.devper.pharm.presentation.reports

import app.devper.pharm.domain.model.DrugProfit
import app.devper.pharm.domain.model.ProfitReport
import app.devper.pharm.domain.model.ProfitSummary
import app.devper.pharm.ui.common.BaseUiState

enum class ProfitSort(val label: String) {
    Profit("กำไรสูง"),
    Margin("Margin %"),
    QtySold("ขายมาก"),
    Revenue("รายได้"),
}

data class ProfitUiState(
    val from: String = "",
    val to: String = "",
    val sort: ProfitSort = ProfitSort.Profit,
    override val loading: Boolean = false,
    val report: ProfitReport? = null,
    val exporting: Boolean = false,
    val message: String? = null,
    override val error: String? = null,
) : BaseUiState {
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
