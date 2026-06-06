package app.devper.pharm.presentation.stock

import app.devper.pharm.domain.model.Drug
import app.devper.pharm.domain.extension.searchByQuery
import app.devper.pharm.ui.common.BaseUiState

data class StockUiState(
    override val loading: Boolean = false,
    val query: String = "",
    val typeFilter: StockTypeFilter = StockTypeFilter.All,
    val drugs: List<Drug> = emptyList(),
    override val error: String? = null,
) : BaseUiState {
    val filtered: List<Drug> = drugs.searchByQuery(query).filter { typeFilter.matches(it.type) }
}
