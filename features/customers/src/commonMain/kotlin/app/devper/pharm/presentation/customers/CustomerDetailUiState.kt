package app.devper.pharm.presentation.customers

import app.devper.pharm.domain.model.Customer
import app.devper.pharm.common.AppException
import app.devper.pharm.domain.model.SaleSummary
import app.devper.pharm.ui.common.BaseUiState

data class CustomerDetailUiState(
    val customer: Customer? = null,
    val sales: List<SaleSummary> = emptyList(),
    val customerLoading: Boolean = false,
    val salesLoading: Boolean = false,
    val errorState: AppException? = null,
) : BaseUiState {

    override val domainError: AppException? get() = errorState
    override val loading: Boolean get() = customerLoading || salesLoading
}
