package app.devper.pharm.presentation.saleshistory

import app.devper.pharm.domain.model.SaleItemSnapshot
import app.devper.pharm.domain.model.SaleSummary
import app.devper.pharm.ui.common.LoadableUiState
import app.devper.pharm.ui.format.DateRangeFilter

data class SalesHistoryUiState(
    val dateRange: DateRangeFilter = DateRangeFilter(),
    val sales: List<SaleSummary> = emptyList(),
    override val loading: Boolean = false,
    val query: String = "",

    val selected: SaleSummary? = null,
    val items: List<SaleItemSnapshot> = emptyList(),
    val itemsLoading: Boolean = false,

    val billSheetOpen: Boolean = false,
    val returnSheetOpen: Boolean = false,

    val returnDraft: Map<String, Int> = emptyMap(),
    val returnReason: String = "",
    val submittingReturn: Boolean = false,
    override val error: String? = null,
) : LoadableUiState<SalesHistoryUiState> {

    override fun withLoading(value: Boolean) = copy(loading = value)
    override fun withError(value: String?) = copy(error = value)
}
