package app.devper.pharm.domain.usecase.purchasing

import app.devper.pharm.domain.usecase.BaseUseCase

import app.devper.pharm.common.AppDispatchers
import app.devper.pharm.domain.repository.PurchaseOrderRepository

class DeletePurchaseOrderUseCase(private val repo: PurchaseOrderRepository, dispatchers: AppDispatchers) :
    BaseUseCase<String, Unit>(dispatchers) {
    override suspend fun execute(param: String) = repo.delete(param)
}
