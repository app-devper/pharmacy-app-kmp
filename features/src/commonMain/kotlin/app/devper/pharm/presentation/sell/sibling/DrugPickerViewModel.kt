package app.devper.pharm.presentation.sell.sibling

import androidx.lifecycle.viewModelScope
import app.devper.pharm.domain.event.StockChangeBus
import app.devper.pharm.domain.model.AltUnit
import app.devper.pharm.domain.model.Drug
import app.devper.pharm.domain.usecase.AddToCartUseCase
import app.devper.pharm.domain.usecase.GetDrugsUseCase
import app.devper.pharm.domain.util.BarcodeMatcher
import app.devper.pharm.domain.util.DrugSearch
import app.devper.pharm.ui.common.BaseViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class DrugPickerViewModel(
    private val getDrugs: GetDrugsUseCase,
    private val addToCart: AddToCartUseCase,
    stockChangeBus: StockChangeBus,
) : BaseViewModel<DrugPickerUiState>(DrugPickerUiState()) {

    init {
        load()

        stockChangeBus.events
            .onEach { load() }
            .launchIn(viewModelScope)
    }

    fun load() {
        setState { copy(drugsLoading = true, error = null) }
        launchResult(
            block = { getDrugs() },
            onSuccess = { list ->
                setState {
                    copy(
                        drugsLoading = false,
                        drugs = list,
                        filteredDrugs = DrugSearch.filter(list, query),
                    )
                }
            },
            onFailure = { e -> setState { copy(drugsLoading = false, error = e.message ?: "โหลดข้อมูลไม่สำเร็จ") } },
        )
    }

    fun onQueryChange(value: String) = setState {
        copy(query = value, filteredDrugs = DrugSearch.filter(drugs, value))
    }

    fun onTapDrug(drug: Drug) {
        if (drug.altUnits.any { !it.hidden }) {
            setState { copy(altUnitPickerFor = drug) }
        } else {
            addToCart(drug, null)
        }
    }

    fun onCloseAltUnitPicker() = setState { copy(altUnitPickerFor = null) }

    fun onPickAltUnit(altUnit: AltUnit?) {
        val drug = current.altUnitPickerFor ?: return
        addToCart(drug, altUnit)
        setState { copy(altUnitPickerFor = null) }
    }

    fun onScanBarcode(code: String) {
        val match = BarcodeMatcher.match(current.drugs, code)
        if (match != null) {
            addToCart(match.drug, match.altUnit)
        } else {
            setState { copy(error = "ไม่พบยาสำหรับบาร์โค้ด ${code.trim()}") }
        }
    }

    fun dismissError() = setState { copy(error = null) }
}
