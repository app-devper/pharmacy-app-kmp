package app.devper.pharm.presentation.stockcount

import app.devper.pharm.domain.model.StockCount
import app.devper.pharm.ui.common.BaseUiState

data class StockCountsListUiState(
    override val loading: Boolean = false,
    val query: String = "",
    val counts: List<StockCount> = emptyList(),
    override val error: String? = null,
) : BaseUiState {
    val filtered: List<StockCount>
        get() {
            if (query.isBlank()) return counts
            val q = query.trim().lowercase()
            return counts.filter { c ->
                c.countNo.lowercase().contains(q) ||
                    c.note.lowercase().contains(q)
            }
        }
}
