package app.devper.pharm.domain.usecase

import app.devper.pharm.common.AppDispatchers
import app.devper.pharm.domain.model.PurchaseOrder
import app.devper.pharm.domain.repository.PurchaseOrderRepository

class ConfirmPurchaseOrderUseCase(private val repo: PurchaseOrderRepository, dispatchers: AppDispatchers) :
    BaseUseCase<String, PurchaseOrder>(dispatchers) {
    override suspend fun execute(param: String): PurchaseOrder = repo.confirm(param)
}
