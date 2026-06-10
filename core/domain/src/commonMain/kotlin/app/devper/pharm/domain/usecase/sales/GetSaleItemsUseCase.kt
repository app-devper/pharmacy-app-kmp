package app.devper.pharm.domain.usecase.sales

import app.devper.pharm.domain.usecase.BaseUseCase

import app.devper.pharm.common.AppDispatchers
import app.devper.pharm.domain.model.SaleItemSnapshot
import app.devper.pharm.domain.repository.SaleHistoryRepository

class GetSaleItemsUseCase(private val repo: SaleHistoryRepository, dispatchers: AppDispatchers) :
    BaseUseCase<String, List<SaleItemSnapshot>>(dispatchers) {
    override suspend fun execute(param: String): List<SaleItemSnapshot> {
        val items = repo.getItems(param)
        val returnedByItem = runCatching { repo.getReturnedQuantities(param) }
            .getOrDefault(emptyMap())
        if (returnedByItem.isEmpty()) return items
        return items.map { it.copy(returnedQty = returnedByItem[it.id] ?: 0) }
    }
}
