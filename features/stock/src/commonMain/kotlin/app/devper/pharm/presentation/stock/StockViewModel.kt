package app.devper.pharm.presentation.stock

import app.devper.pharm.common.error.CommonUiStateError
import app.devper.pharm.domain.param.inventory.ExpiringLotsFilterParam
import app.devper.pharm.domain.usecase.inventory.GetDrugsUseCase
import app.devper.pharm.domain.usecase.inventory.GetExpiringLotsUseCase
import app.devper.pharm.ui.common.BaseLoadableViewModel

private const val EXPIRING_SOON_DAYS = 90

class StockViewModel(
    private val getDrugs: GetDrugsUseCase,
    private val getExpiringLots: GetExpiringLotsUseCase,
) : BaseLoadableViewModel<StockUiState>(StockUiState()) {

    init { reload() }

    fun onQueryChange(value: String) = setState { copy(query = value) }
    fun onTypeFilterChange(value: StockTypeFilter) = setState { copy(typeFilter = value) }

    fun reload() {
        setState { copy(loading = true, errorState = null) }
        launchResult(
            block = { getDrugs() },
            onSuccess = { list -> setState { copy(loading = false, drugs = list) } },
            onFailure = { e -> setState { copy(loading = false, errorState = CommonUiStateError.LoadFailed(e)) } },
        )
        launchResult(
            block = { getExpiringLots(ExpiringLotsFilterParam(daysAhead = EXPIRING_SOON_DAYS)) },
            onSuccess = { lots -> setState { copy(expiringSoonCount = lots.size) } },
            onFailure = { setState { copy(expiringSoonCount = null) } },
        )
    }
}
