package app.devper.pharm.presentation.sell.flow

import app.devper.pharm.ui.common.BaseUiState

data class VoidSaleUiState(
    val sheetOpen: Boolean = false,
    val submitting: Boolean = false,
    override val error: String? = null,
) : BaseUiState {

    override val loading: Boolean get() = submitting
}
