package app.devper.pharm.presentation.sell.flow

import app.devper.pharm.domain.model.Drug
import app.devper.pharm.common.AppException
import app.devper.pharm.ui.common.BaseUiState

data class DrugPickerUiState(
    val drugs: List<Drug> = emptyList(),
    val drugsLoading: Boolean = false,

    val query: String = "",

    val filteredDrugs: List<Drug> = emptyList(),

    val altUnitPickerFor: Drug? = null,
    val errorState: AppException? = null,
) : BaseUiState {

    override val domainError: AppException? get() = errorState
    override val loading: Boolean get() = drugsLoading
}
