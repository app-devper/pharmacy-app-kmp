package app.devper.pharm.data.repository.internal

import app.devper.pharm.common.value.Money
import app.devper.pharm.data.remote.dto.SaleItemRequest
import app.devper.pharm.data.remote.dto.SaleRequest
import app.devper.pharm.data.remote.dto.SaleResponse
import app.devper.pharm.domain.model.Sale
import app.devper.pharm.domain.model.StockUpdate
import app.devper.pharm.domain.param.CheckoutLineParam
import app.devper.pharm.domain.param.CheckoutParam

internal fun SaleResponse.toDomain(): Sale = Sale(
    id = id,
    billNo = billNo,
    total = Money(total),
    change = Money(change),
    discount = Money(discount),
    stockUpdates = stockUpdates.map { StockUpdate(it.drugId, it.newStock) },
    kySkippedByCashier = kySkippedByCashier,
)

internal fun CheckoutParam.toRequest(): SaleRequest = SaleRequest(
    items = items.map { it.toRequest() },
    received = received.amount,
    discount = discount.amount,
    customerId = customerId,
    clientRequestId = clientRequestId,
    kySkippedByCashier = kySkippedByCashier,
)

internal fun CheckoutLineParam.toRequest(): SaleItemRequest = SaleItemRequest(
    drugId = drugId,
    qty = qty,
    price = unitPrice.amount,
    originalPrice = originalUnitPrice.amount,
    itemDiscount = itemDiscount.amount,
    priceTier = priceTier,
    allowOversell = allowOversell,
    unit = unit,
    unitFactor = unitFactor,
)
