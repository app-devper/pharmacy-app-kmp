package app.devper.pharm.presentation.stock

import app.devper.pharm.domain.model.StockMovement
import app.devper.pharm.ui.common.BaseUiState

data class DrugHistoryUiState(
    val drugName: String = "",
    val items: List<StockMovement> = emptyList(),
    override val loading: Boolean = false,
    override val error: String? = null,
) : BaseUiState
