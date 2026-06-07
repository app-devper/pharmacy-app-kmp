package app.devper.pharm.presentation.planning

import app.devper.pharm.domain.model.Drug
import app.devper.pharm.ui.common.LoadableUiState

data class LowStockUiState(
    override val loading: Boolean = false,
    val drugs: List<Drug> = emptyList(),
    override val error: String? = null,
) : LoadableUiState<LowStockUiState> {

    override fun withLoading(value: Boolean) = copy(loading = value)
    override fun withError(value: String?) = copy(error = value)
}
