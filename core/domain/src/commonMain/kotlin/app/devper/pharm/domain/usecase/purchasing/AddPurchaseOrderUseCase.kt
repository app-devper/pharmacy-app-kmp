package app.devper.pharm.domain.usecase.purchasing

import app.devper.pharm.domain.usecase.BaseUseCase

import app.devper.pharm.common.AppDispatchers
import app.devper.pharm.domain.model.PurchaseOrder
import app.devper.pharm.domain.param.purchasing.AddPurchaseOrderParam
import app.devper.pharm.domain.repository.purchasing.PurchaseOrderRepository

class AddPurchaseOrderUseCase(private val repo: PurchaseOrderRepository, dispatchers: AppDispatchers) :
    BaseUseCase<AddPurchaseOrderParam, PurchaseOrder>(dispatchers) {
    override suspend fun execute(param: AddPurchaseOrderParam): PurchaseOrder = repo.add(param)
}
