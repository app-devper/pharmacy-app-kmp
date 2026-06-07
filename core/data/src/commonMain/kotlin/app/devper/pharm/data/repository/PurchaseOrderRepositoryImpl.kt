package app.devper.pharm.data.repository

import app.devper.pharm.data.internal.parseLocalDateOrNull
import app.devper.pharm.data.internal.parseLocalDateTimeOrNull
import app.devper.pharm.data.internal.toIso
import app.devper.pharm.data.remote.api.PurchaseOrderApi
import app.devper.pharm.data.remote.dto.PurchaseOrderDto
import app.devper.pharm.data.remote.dto.PurchaseOrderInputDto
import app.devper.pharm.data.remote.dto.PurchaseOrderItemDto
import app.devper.pharm.data.remote.dto.PurchaseOrderItemInputDto
import app.devper.pharm.data.remote.dto.PurchaseOrderSummaryDto
import app.devper.pharm.domain.event.StockChangeBus
import app.devper.pharm.domain.model.PurchaseOrder
import app.devper.pharm.domain.model.PurchaseOrderItem
import app.devper.pharm.domain.model.PurchaseOrderStatus
import app.devper.pharm.domain.model.PurchaseOrderSummary
import app.devper.pharm.domain.param.AddPurchaseOrderParam
import app.devper.pharm.domain.param.PurchaseOrderItemInput
import app.devper.pharm.domain.param.UpdatePurchaseOrderParam
import app.devper.pharm.domain.repository.PurchaseOrderRepository

class PurchaseOrderRepositoryImpl(
    private val api: PurchaseOrderApi,
    private val stockChangeBus: StockChangeBus,
) : PurchaseOrderRepository {

    override suspend fun list(): List<PurchaseOrderSummary> = api.list().map(::toSummary)

    override suspend fun get(id: String): PurchaseOrder = toDomain(api.get(id))

    override suspend fun add(param: AddPurchaseOrderParam): PurchaseOrder =
        toDomain(api.add(param.toDto()))

    override suspend fun update(param: UpdatePurchaseOrderParam): PurchaseOrder =
        toDomain(api.update(param.id, param.toDto()))

    override suspend fun confirm(id: String): PurchaseOrder {

        val result = toDomain(api.confirm(id))
        stockChangeBus.emit()
        return result
    }

    override suspend fun delete(id: String) {
        api.delete(id)
    }

    private fun toDomain(d: PurchaseOrderDto) = PurchaseOrder(
        id = d.id,
        docNo = d.docNo,
        supplier = d.supplier,
        invoiceNo = d.invoiceNo,
        receiveDate = d.receiveDate.parseLocalDateOrNull(),
        items = d.items.map(::toItem),
        itemCount = d.itemCount,
        totalCost = d.totalCost,
        status = PurchaseOrderStatus.fromWire(d.status),
        notes = d.notes,
        createdAt = d.createdAt.parseLocalDateTimeOrNull(),
        confirmedAt = d.confirmedAt.parseLocalDateTimeOrNull(),
    )

    private fun toSummary(d: PurchaseOrderSummaryDto) = PurchaseOrderSummary(
        id = d.id,
        docNo = d.docNo,
        supplier = d.supplier,
        invoiceNo = d.invoiceNo,
        receiveDate = d.receiveDate.parseLocalDateOrNull(),
        itemCount = d.itemCount,
        totalCost = d.totalCost,
        status = PurchaseOrderStatus.fromWire(d.status),
        notes = d.notes,
        createdAt = d.createdAt.parseLocalDateTimeOrNull(),
        confirmedAt = d.confirmedAt.parseLocalDateTimeOrNull(),
    )

    private fun toItem(d: PurchaseOrderItemDto) = PurchaseOrderItem(
        drugId = d.drugId,
        drugName = d.drugName,
        lotNumber = d.lotNumber,
        expiryDate = d.expiryDate.parseLocalDateOrNull(),
        qty = d.qty,
        costPrice = d.costPrice,
        sellPrice = d.sellPrice,
    )

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
