package app.devper.pharm.data.repository.internal

import app.devper.pharm.common.value.Money
import app.devper.pharm.data.remote.dto.SaleItemDto
import app.devper.pharm.domain.model.SaleItemSnapshot

internal fun SaleItemDto.toDomain(returnedQty: Int): SaleItemSnapshot = SaleItemSnapshot(
    id = id,
    drugId = drugId,
    drugName = drugName,
    qty = qty,
    price = Money(price),
    originalPrice = Money(originalPrice),
    itemDiscount = Money(itemDiscount),
    unit = unit,
    unitFactor = unitFactor,
    priceTier = priceTier,
    returnedQty = returnedQty,
    lotBoundQty = lotSplits
        .filter { it.lotId.isNotBlank() && it.lotId != ZERO_OBJECT_ID }
        .sumOf { it.qty },
)

private const val ZERO_OBJECT_ID = "000000000000000000000000"

