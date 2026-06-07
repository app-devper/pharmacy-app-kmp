package app.devper.pharm.data.repository.internal

import app.devper.pharm.data.remote.dto.SaleItemDto
import app.devper.pharm.domain.model.SaleItemSnapshot

internal fun SaleItemDto.toDomain(returnedQty: Int): SaleItemSnapshot = SaleItemSnapshot(
    id = id,
    drugId = drugId,
    drugName = drugName,
    qty = qty,
    price = price,
    originalPrice = originalPrice,
    itemDiscount = itemDiscount,
    unit = unit,
    unitFactor = unitFactor,
    priceTier = priceTier,
    returnedQty = returnedQty,
)
