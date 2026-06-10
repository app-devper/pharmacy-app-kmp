package app.devper.pharm.domain.repository

import app.devper.pharm.domain.repository.purchasing.PurchaseOrderRepository

import app.devper.pharm.common.value.Money
import app.devper.pharm.domain.model.PurchaseOrder
import app.devper.pharm.domain.model.PurchaseOrderItem
import app.devper.pharm.domain.model.PurchaseOrderStatus
import app.devper.pharm.domain.model.PurchaseOrderSummary
import app.devper.pharm.domain.param.purchasing.AddPurchaseOrderParam
import app.devper.pharm.domain.param.purchasing.PurchaseOrderItemInput
import app.devper.pharm.domain.param.purchasing.UpdatePurchaseOrderParam

class FakePurchaseOrderRepository(
    private val seed: Map<String, PurchaseOrder> = emptyMap(),
    private val listSeed: List<PurchaseOrderSummary> = emptyList(),
    private val getThrows: Boolean = false,
    private val addThrowsOn: String? = null,
) : PurchaseOrderRepository {

    var lastAdd: AddPurchaseOrderParam? = null
        private set
    var lastUpdate: UpdatePurchaseOrderParam? = null
        private set
    var lastConfirm: String? = null
        private set
    var lastDelete: String? = null
        private set

    override suspend fun list(): List<PurchaseOrderSummary> = listSeed

    override suspend fun get(id: String): PurchaseOrder {
        if (getThrows) throw RuntimeException("get failed")
        return seed[id] ?: throw IllegalStateException("ไม่พบใบรับ")
    }

    override suspend fun add(param: AddPurchaseOrderParam): PurchaseOrder {
        if (addThrowsOn != null && param.supplier == addThrowsOn) {
            throw RuntimeException("backend rejected: $addThrowsOn")
        }
        lastAdd = param
        return synthesise(id = "new-po", param.supplier, param.invoiceNo, param.receiveDate, param.notes, param.items)
    }

    override suspend fun update(param: UpdatePurchaseOrderParam): PurchaseOrder {
        lastUpdate = param
        return synthesise(param.id, param.supplier, param.invoiceNo, param.receiveDate, param.notes, param.items)
    }

    override suspend fun confirm(id: String): PurchaseOrder {
        lastConfirm = id
        return seed[id]?.copy(status = PurchaseOrderStatus.Confirmed)
            ?: throw IllegalStateException("ไม่พบใบรับ $id")
    }

    override suspend fun delete(id: String) {
        lastDelete = id
    }

    private fun synthesise(
        id: String,
        supplier: String,
        invoiceNo: String,
        receiveDate: kotlinx.datetime.LocalDate?,
        notes: String,
        items: List<PurchaseOrderItemInput>,
    ): PurchaseOrder = PurchaseOrder(
        id = id,
        docNo = "PO-$id",
        supplier = supplier,
        invoiceNo = invoiceNo,
        receiveDate = receiveDate,
        items = items.map { input ->
            PurchaseOrderItem(
                drugId = input.drugId,
                drugName = input.drugName,
                lotNumber = input.lotNumber,
                expiryDate = input.expiryDate,
                qty = input.qty,
                costPrice = input.costPrice,
                sellPrice = input.sellPrice,
            )
        },
        itemCount = items.size,
        totalCost = items.fold(Money.Zero) { acc, item -> acc + item.costPrice * item.qty.value },
        status = PurchaseOrderStatus.Draft,
        notes = notes,
        createdAt = kotlinx.datetime.LocalDateTime.parse("2026-05-14T10:00:00"),
        confirmedAt = null,
    )
}
