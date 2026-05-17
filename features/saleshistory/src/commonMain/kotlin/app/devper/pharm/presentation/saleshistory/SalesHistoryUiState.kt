package app.devper.pharm.presentation.saleshistory

import app.devper.pharm.domain.model.SaleItemSnapshot
import app.devper.pharm.domain.model.SaleSummary
import app.devper.pharm.ui.common.BaseUiState

data class SalesHistoryUiState(
    val sales: List<SaleSummary> = emptyList(),
    override val loading: Boolean = false,
    val from: String = "",
    val to: String = "",
    val query: String = "",

    val selected: SaleSummary? = null,
    val items: List<SaleItemSnapshot> = emptyList(),
    val itemsLoading: Boolean = false,

    val returnSheetOpen: Boolean = false,

    val returnDraft: Map<String, Int> = emptyMap(),
    val returnReason: String = "",
    val submittingReturn: Boolean = false,
    override val error: String? = null,
) : BaseUiState
