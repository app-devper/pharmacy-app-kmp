package app.devper.pharm.presentation.stock

import app.devper.pharm.common.AppException
import app.devper.pharm.domain.model.StockMovement
import app.devper.pharm.ui.common.BaseUiState

data class DrugHistoryUiState(
    val drugName: String = "",
    val items: List<StockMovement> = emptyList(),
    override val loading: Boolean = false,
    val errorState: AppException? = null,
) : BaseUiState {
    override val domainError: AppException? get() = errorState
}
