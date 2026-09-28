package app.devper.pharm.data.repository.internal

import app.devper.pharm.common.value.Money
import app.devper.pharm.data.remote.dto.SaleItemRequest
import app.devper.pharm.data.remote.dto.SaleRequest
import app.devper.pharm.data.remote.dto.SaleResponse
import app.devper.pharm.domain.model.Sale
import app.devper.pharm.domain.model.StockUpdate
import app.devper.pharm.domain.param.sales.CheckoutLineParam
import app.devper.pharm.domain.param.sales.CheckoutParam
import app.devper.pharm.data.remote.dto.Ky10CaptureRequest
import app.devper.pharm.data.remote.dto.Ky11CaptureRequest
import app.devper.pharm.data.remote.dto.Ky12CaptureRequest
import app.devper.pharm.data.remote.dto.SaleKyCaptureRequest
import app.devper.pharm.domain.model.SaleKyCapture

internal fun SaleResponse.toDomain(): Sale = Sale(
    id = id,
    billNo = billNo,
    total = Money(total),
    change = Money(change),
    discount = Money(discount),
    stockUpdates = stockUpdates.map { StockUpdate(it.drugId, it.newStock) },
    kySkippedByCashier = kySkippedByCashier,
)

internal fun SaleKyCapture.toRequest(): SaleKyCaptureRequest = SaleKyCaptureRequest(
    ky10 = ky10?.let { Ky10CaptureRequest(it.ky10BuyerName, it.ky10BuyerAddress, it.ky10RxNo, it.ky10Doctor) },
    ky11 = ky11?.let { Ky11CaptureRequest(it.ky11BuyerName, it.ky11Purpose, it.ky11Pharmacist) },
    ky12 = ky12?.let { Ky12CaptureRequest(it.ky12RxNo, it.ky12PatientName, it.ky12Doctor, it.ky12Hospital, it.ky12Status) },
)

internal fun CheckoutParam.toRequest(): SaleRequest = SaleRequest(
    items = items.map { it.toRequest() },
    received = received.amount,
    discount = discount.amount,
    customerId = customerId,
    clientRequestId = clientRequestId,
    kySkippedByCashier = kySkippedByCashier,
    ky = ky?.toRequest(),
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
