package app.devper.pharm.domain.usecase

import app.devper.pharm.common.AppDispatchers
import app.devper.pharm.domain.model.PurchaseOrderSummary
import app.devper.pharm.domain.repository.PurchaseOrderRepository

class GetPurchaseOrdersUseCase(private val repo: PurchaseOrderRepository, dispatchers: AppDispatchers) :
    BaseQueryUseCase<List<PurchaseOrderSummary>>(dispatchers) {
    override suspend fun execute(param: Unit): List<PurchaseOrderSummary> = repo.list()
}
