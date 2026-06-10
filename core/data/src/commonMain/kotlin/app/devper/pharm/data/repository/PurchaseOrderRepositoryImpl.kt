package app.devper.pharm.data.repository

import app.devper.pharm.data.remote.api.PurchaseOrderApi
import app.devper.pharm.data.repository.internal.toDomain
import app.devper.pharm.data.repository.internal.toDto
import app.devper.pharm.domain.event.StockChangeBus
import app.devper.pharm.domain.model.PurchaseOrder
import app.devper.pharm.domain.model.PurchaseOrderSummary
import app.devper.pharm.domain.param.purchasing.AddPurchaseOrderParam
import app.devper.pharm.domain.param.purchasing.UpdatePurchaseOrderParam
import app.devper.pharm.domain.repository.purchasing.PurchaseOrderRepository

class PurchaseOrderRepositoryImpl(
    private val api: PurchaseOrderApi,
    private val stockChangeBus: StockChangeBus,
) : PurchaseOrderRepository {

    override suspend fun list(): List<PurchaseOrderSummary> = api.list().map { it.toDomain() }

    override suspend fun get(id: String): PurchaseOrder = api.get(id).toDomain()

    override suspend fun add(param: AddPurchaseOrderParam): PurchaseOrder =
        api.add(param.toDto()).toDomain()

    override suspend fun update(param: UpdatePurchaseOrderParam): PurchaseOrder =
        api.update(param.id, param.toDto()).toDomain()

    override suspend fun confirm(id: String): PurchaseOrder {

        val result = api.confirm(id).toDomain()
        stockChangeBus.emit()
        return result
    }

    override suspend fun delete(id: String) {
        api.delete(id)
    }
}
