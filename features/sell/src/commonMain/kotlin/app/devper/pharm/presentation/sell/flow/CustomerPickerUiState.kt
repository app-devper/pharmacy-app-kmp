package app.devper.pharm.presentation.sell.flow

import app.devper.pharm.domain.model.Customer
import app.devper.pharm.common.AppException
import app.devper.pharm.ui.common.BaseUiState

data class CustomerPickerUiState(
    val customers: List<Customer> = emptyList(),
    override val loading: Boolean = false,
    val open: Boolean = false,
    val errorState: AppException? = null,
) : BaseUiState {
    override val domainError: AppException? get() = errorState
}
