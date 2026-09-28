package app.devper.pharm.domain.usecase.sales

import app.devper.pharm.domain.usecase.BaseUseCase

import app.devper.pharm.common.AppDispatchers
import app.devper.pharm.domain.model.SaleItemSnapshot
import app.devper.pharm.domain.repository.sales.SaleHistoryRepository

class GetSaleItemsUseCase(private val repo: SaleHistoryRepository, dispatchers: AppDispatchers) :
    BaseUseCase<String, List<SaleItemSnapshot>>(dispatchers) {
    /** Each line carries what it can still return, from the server. */
    override suspend fun execute(param: String): List<SaleItemSnapshot> = repo.getItems(param)
}
