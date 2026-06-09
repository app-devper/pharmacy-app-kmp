package app.devper.pharm.presentation.movements

import app.devper.pharm.common.AppException
import app.devper.pharm.common.error.CommonUiStateMessage
import app.devper.pharm.domain.model.StockMovement
import app.devper.pharm.ui.common.LoadableUiState
import app.devper.pharm.ui.format.DateRangeFilter

data class MovementsUiState(
    val dateRange: DateRangeFilter = DateRangeFilter(),
    val items: List<StockMovement> = emptyList(),
    val total: Int = 0,
    override val loading: Boolean = false,
    val drugName: String = "",
    val activeTypeIds: Set<String> = MovementsTypeCatalog.allIds,
    val page: Int = 1,
    val pageSize: Int = 20,
    val exporting: Boolean = false,
    val message: String? = null,
    val messageState: CommonUiStateMessage? = null,
    val errorState: AppException? = null,
) : LoadableUiState<MovementsUiState> {

    override val domainError: AppException? get() = errorState
    override fun withLoading(value: Boolean) = copy(loading = value)
    override fun withError(value: String?) = if (value == null) copy(errorState = null) else this

    val pageCount: Int = run {
        if (pageSize <= 0) 1
        else {
            val c = (items.size + pageSize - 1) / pageSize
            if (c <= 0) 1 else c
        }
    }

    val pageItems: List<StockMovement> = run {
        if (items.isEmpty()) emptyList()
        else {
            val start = ((page - 1).coerceAtLeast(0)) * pageSize
            if (start >= items.size) emptyList()
            else {
                val end = (start + pageSize).coerceAtMost(items.size)
                items.subList(start, end)
            }
        }
    }
}
