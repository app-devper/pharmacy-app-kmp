package app.devper.pharm.presentation.stock

import app.devper.pharm.domain.param.reports.ExportDrugsCsvParam
import app.devper.pharm.domain.usecase.reports.ExportDrugsCsvUseCase
import app.devper.pharm.domain.param.inventory.ExpiringLotsFilterParam
import app.devper.pharm.domain.repository.inventory.DrugRepository
import app.devper.pharm.domain.repository.inventory.ExpiringLotsRepository
import app.devper.pharm.ui.common.BaseExportableViewModel
import app.devper.pharm.presentation.stock.exception.StockUiStateError

private const val EXPIRING_SOON_DAYS = 90

class StockViewModel(
    private val drugs: DrugRepository,
    private val expiringLots: ExpiringLotsRepository,
    private val exportDrugsCsv: ExportDrugsCsvUseCase,
) : BaseExportableViewModel<StockUiState>(StockUiState()) {

    init { reload() }

    fun onQueryChange(value: String) = setState { copy(query = value) }
    fun onTypeFilterChange(value: StockTypeFilter) = setState { copy(typeFilter = value) }

    fun onExportExcel(headers: List<String>) {
        export(current.filtered) { rows -> exportDrugsCsv(ExportDrugsCsvParam(rows = rows, headers = headers)) }
    }

    fun reload() {
        load(
            fetch = { Result.success(drugs.list()) },
            failure = { e -> StockUiStateError.LoadStockFailed(e) },
            onLoaded = { list -> copy(drugs = list) },
        )
        launchResult(
            block = { Result.success(expiringLots.list(ExpiringLotsFilterParam(daysAhead = EXPIRING_SOON_DAYS))) },
            onSuccess = { lots -> setState { copy(expiringSoonCount = lots.size) } },
            onFailure = { setState { copy(expiringSoonCount = null) } },
        )
    }
}
