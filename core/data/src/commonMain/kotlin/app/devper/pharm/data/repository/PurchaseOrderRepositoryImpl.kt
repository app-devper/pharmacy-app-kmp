package app.devper.pharm.data.repository

import app.devper.pharm.data.internal.toIso
import app.devper.pharm.data.remote.api.PurchaseOrderApi
import app.devper.pharm.data.remote.dto.PurchaseOrderInputDto
import app.devper.pharm.data.remote.dto.PurchaseOrderItemInputDto
import app.devper.pharm.data.repository.internal.toDomain
import app.devper.pharm.domain.event.StockChangeBus
import app.devper.pharm.domain.model.PurchaseOrder
import app.devper.pharm.domain.model.PurchaseOrderSummary
import app.devper.pharm.domain.param.AddPurchaseOrderParam
import app.devper.pharm.domain.param.PurchaseOrderItemInput
import app.devper.pharm.domain.param.UpdatePurchaseOrderParam
import app.devper.pharm.domain.repository.PurchaseOrderRepository

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

    private fun AddPurchaseOrderParam.toDto() = PurchaseOrderInputDto(
        supplier = supplier.trim(),
        invoiceNo = invoiceNo.trim(),
        receiveDate = receiveDate?.toIso() ?: "",
        notes = notes.trim(),
        items = items.map { it.toDto() },
    )

    private fun UpdatePurchaseOrderParam.toDto() = PurchaseOrderInputDto(
        supplier = supplier.trim(),
        invoiceNo = invoiceNo.trim(),
        receiveDate = receiveDate?.toIso() ?: "",
        notes = notes.trim(),
        items = items.map { it.toDto() },
    )

    private fun PurchaseOrderItemInput.toDto() = PurchaseOrderItemInputDto(
        drugId = drugId,
        drugName = drugName.trim(),
        lotNumber = lotNumber.trim(),
        expiryDate = expiryDate.toIso(),
        qty = qty,
        costPrice = costPrice,
        sellPrice = sellPrice,
    )
}
