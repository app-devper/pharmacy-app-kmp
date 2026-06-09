package app.devper.pharm.presentation.sell.flow

import app.devper.pharm.common.AppException
import app.devper.pharm.ui.common.BaseUiState

data class VoidSaleUiState(
    val sheetOpen: Boolean = false,
    val submitting: Boolean = false,
    val errorState: AppException? = null,
) : BaseUiState {

    override val domainError: AppException? get() = errorState
    override val loading: Boolean get() = submitting
}
