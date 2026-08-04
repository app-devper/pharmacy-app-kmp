package app.devper.pharm.presentation.stock

import app.devper.pharm.common.error.CommonUiStateMessage
import app.devper.pharm.common.error.CommonUiStateError
import app.devper.pharm.domain.param.reports.ExportDrugsCsvParam
import app.devper.pharm.domain.usecase.reports.ExportDrugsCsvUseCase
import app.devper.pharm.domain.param.inventory.ExpiringLotsFilterParam
import app.devper.pharm.domain.usecase.inventory.GetDrugsUseCase
import app.devper.pharm.domain.usecase.inventory.GetExpiringLotsUseCase
import app.devper.pharm.ui.common.BaseLoadableViewModel
import app.devper.pharm.presentation.stock.exception.StockUiStateError

private const val EXPIRING_SOON_DAYS = 90

class StockViewModel(
    private val getDrugs: GetDrugsUseCase,
    private val getExpiringLots: GetExpiringLotsUseCase,
    private val exportDrugsCsv: ExportDrugsCsvUseCase,
) : BaseLoadableViewModel<StockUiState>(StockUiState()) {

    init { reload() }

    fun onQueryChange(value: String) = setState { copy(query = value) }
    fun onTypeFilterChange(value: StockTypeFilter) = setState { copy(typeFilter = value) }

    fun onExportExcel(headers: List<String>) {
        val rows = current.filtered
        if (rows.isEmpty()) {
            setState { copy(messageState = CommonUiStateMessage.ExportEmpty) }
            return
        }
        setState { copy(exporting = true) }
        launchResult(
            block = { exportDrugsCsv(ExportDrugsCsvParam(rows = rows, headers = headers)) },
            onSuccess = { path -> setState { copy(exporting = false, messageState = CommonUiStateMessage.ExportDone(path)) } },
            onFailure = { e -> setState { copy(exporting = false, errorState = CommonUiStateError.ExportFailed(e)) } },
        )
    }

    fun dismissMessage() = setState { copy(messageState = null) }

    fun reload() {
        setState { copy(loading = true, errorState = null) }
        launchResult(
            block = { getDrugs() },
            onSuccess = { list -> setState { copy(loading = false, drugs = list) } },
            onFailure = { e ->
                setState { copy(loading = false, errorState = StockUiStateError.LoadStockFailed(e)) }
            },
        )
        launchResult(
            block = { getExpiringLots(ExpiringLotsFilterParam(daysAhead = EXPIRING_SOON_DAYS)) },
            onSuccess = { lots -> setState { copy(expiringSoonCount = lots.size) } },
            onFailure = { setState { copy(expiringSoonCount = null) } },
        )
    }
}
