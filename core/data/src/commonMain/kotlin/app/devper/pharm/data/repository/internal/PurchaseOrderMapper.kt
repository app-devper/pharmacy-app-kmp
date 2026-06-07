package app.devper.pharm.data.repository.internal

import app.devper.pharm.common.value.Money
import app.devper.pharm.common.value.Quantity
import app.devper.pharm.data.internal.parseLocalDateOrNull
import app.devper.pharm.data.internal.parseLocalDateTimeOrNull
import app.devper.pharm.data.internal.toIso
import app.devper.pharm.data.remote.dto.PurchaseOrderDto
import app.devper.pharm.data.remote.dto.PurchaseOrderInputDto
import app.devper.pharm.data.remote.dto.PurchaseOrderItemDto
import app.devper.pharm.data.remote.dto.PurchaseOrderItemInputDto
import app.devper.pharm.data.remote.dto.PurchaseOrderSummaryDto
import app.devper.pharm.domain.model.PurchaseOrder
import app.devper.pharm.domain.model.PurchaseOrderItem
import app.devper.pharm.domain.model.PurchaseOrderStatus
import app.devper.pharm.domain.model.PurchaseOrderSummary
import app.devper.pharm.domain.param.AddPurchaseOrderParam
import app.devper.pharm.domain.param.PurchaseOrderItemInput
import app.devper.pharm.domain.param.UpdatePurchaseOrderParam

internal fun PurchaseOrderDto.toDomain(): PurchaseOrder = PurchaseOrder(
    id = id,
    docNo = docNo,
    supplier = supplier,
    invoiceNo = invoiceNo,
    receiveDate = receiveDate.parseLocalDateOrNull(),
    items = items.map { it.toDomain() },
    itemCount = itemCount,
    totalCost = Money(totalCost),
    status = PurchaseOrderStatus.fromWire(status),
    notes = notes,
    createdAt = createdAt.parseLocalDateTimeOrNull(),
    confirmedAt = confirmedAt.parseLocalDateTimeOrNull(),
)

internal fun PurchaseOrderSummaryDto.toDomain(): PurchaseOrderSummary = PurchaseOrderSummary(
    id = id,
    docNo = docNo,
    supplier = supplier,
    invoiceNo = invoiceNo,
    receiveDate = receiveDate.parseLocalDateOrNull(),
    itemCount = itemCount,
    totalCost = Money(totalCost),
    status = PurchaseOrderStatus.fromWire(status),
    notes = notes,
    createdAt = createdAt.parseLocalDateTimeOrNull(),
    confirmedAt = confirmedAt.parseLocalDateTimeOrNull(),
)

internal fun PurchaseOrderItemDto.toDomain(): PurchaseOrderItem = PurchaseOrderItem(
    drugId = drugId,
    drugName = drugName,
    lotNumber = lotNumber,
    expiryDate = expiryDate.parseLocalDateOrNull(),
    qty = Quantity(qty),
    costPrice = Money(costPrice),
    sellPrice = sellPrice?.let(::Money),
)

internal fun AddPurchaseOrderParam.toDto(): PurchaseOrderInputDto = PurchaseOrderInputDto(
    supplier = supplier.trim(),
    invoiceNo = invoiceNo.trim(),
    receiveDate = receiveDate?.toIso() ?: "",
    notes = notes.trim(),
    items = items.map { it.toDto() },
)

internal fun UpdatePurchaseOrderParam.toDto(): PurchaseOrderInputDto = PurchaseOrderInputDto(
    supplier = supplier.trim(),
    invoiceNo = invoiceNo.trim(),
    receiveDate = receiveDate?.toIso() ?: "",
    notes = notes.trim(),
    items = items.map { it.toDto() },
)

internal fun PurchaseOrderItemInput.toDto(): PurchaseOrderItemInputDto = PurchaseOrderItemInputDto(
    drugId = drugId,
    drugName = drugName.trim(),
    lotNumber = lotNumber.trim(),
    expiryDate = expiryDate.toIso(),
    qty = qty.value,
    costPrice = costPrice.amount,
    sellPrice = sellPrice?.amount,
)
