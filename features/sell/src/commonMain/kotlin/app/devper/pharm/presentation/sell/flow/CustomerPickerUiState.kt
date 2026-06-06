package app.devper.pharm.presentation.sell.flow

import app.devper.pharm.domain.model.Customer
import app.devper.pharm.ui.common.BaseUiState

data class CustomerPickerUiState(
    val customers: List<Customer> = emptyList(),
    override val loading: Boolean = false,
    val open: Boolean = false,
    override val error: String? = null,
) : BaseUiState
