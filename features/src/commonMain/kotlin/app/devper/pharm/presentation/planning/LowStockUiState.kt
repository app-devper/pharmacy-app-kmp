package app.devper.pharm.presentation.planning

import app.devper.pharm.domain.model.Drug
import app.devper.pharm.ui.common.BaseUiState

data class LowStockUiState(
    override val loading: Boolean = false,
    val drugs: List<Drug> = emptyList(),
    override val error: String? = null,
) : BaseUiState
