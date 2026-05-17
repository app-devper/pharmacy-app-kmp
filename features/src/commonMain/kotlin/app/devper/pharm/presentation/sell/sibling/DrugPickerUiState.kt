package app.devper.pharm.presentation.sell.sibling

import app.devper.pharm.domain.model.Drug
import app.devper.pharm.domain.util.DrugSearch
import app.devper.pharm.ui.common.BaseUiState

data class DrugPickerUiState(
    val drugs: List<Drug> = emptyList(),
    val drugsLoading: Boolean = false,

    val query: String = "",

    val altUnitPickerFor: Drug? = null,
    override val error: String? = null,
) : BaseUiState {

    override val loading: Boolean get() = drugsLoading

    val filteredDrugs: List<Drug> get() = DrugSearch.filter(drugs, query)
}
