package app.devper.pharm.presentation.movements

import app.devper.pharm.domain.model.StockMovement
import app.devper.pharm.ui.common.BaseUiState

data class MovementsUiState(
    val items: List<StockMovement> = emptyList(),
    val total: Int = 0,
    override val loading: Boolean = false,
    val from: String = "",
    val to: String = "",
    val drugName: String = "",
    val activeTypeIds: Set<String> = MovementsTypeCatalog.allIds,
    val page: Int = 1,
    val pageSize: Int = 20,
    val exporting: Boolean = false,
    val message: String? = null,
    override val error: String? = null,
) : BaseUiState {

    val pageCount: Int
        get() {
            if (pageSize <= 0) return 1
            val c = (items.size + pageSize - 1) / pageSize
            return if (c <= 0) 1 else c
        }

    val pageItems: List<StockMovement>
        get() {
            if (items.isEmpty()) return emptyList()
            val start = ((page - 1).coerceAtLeast(0)) * pageSize
            if (start >= items.size) return emptyList()
            val end = (start + pageSize).coerceAtMost(items.size)
            return items.subList(start, end)
        }
}
