package app.devper.pharm.domain.usecase.purchasing

import app.devper.pharm.domain.usecase.BaseUseCase

import app.devper.pharm.common.AppDispatchers
import app.devper.pharm.domain.model.PurchaseOrder
import app.devper.pharm.domain.param.UpdatePurchaseOrderParam
import app.devper.pharm.domain.repository.PurchaseOrderRepository

class UpdatePurchaseOrderUseCase(private val repo: PurchaseOrderRepository, dispatchers: AppDispatchers) :
    BaseUseCase<UpdatePurchaseOrderParam, PurchaseOrder>(dispatchers) {
    override suspend fun execute(param: UpdatePurchaseOrderParam): PurchaseOrder = repo.update(param)
}
