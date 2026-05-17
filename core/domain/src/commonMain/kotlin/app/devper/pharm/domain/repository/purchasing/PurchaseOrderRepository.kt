package app.devper.pharm.domain.repository

import app.devper.pharm.domain.model.PurchaseOrder
import app.devper.pharm.domain.model.PurchaseOrderSummary
import app.devper.pharm.domain.param.AddPurchaseOrderParam
import app.devper.pharm.domain.param.UpdatePurchaseOrderParam

interface PurchaseOrderRepository {
    suspend fun list(): List<PurchaseOrderSummary>
    suspend fun get(id: String): PurchaseOrder
    suspend fun add(param: AddPurchaseOrderParam): PurchaseOrder
    suspend fun update(param: UpdatePurchaseOrderParam): PurchaseOrder
    suspend fun confirm(id: String): PurchaseOrder
    suspend fun delete(id: String)
}
