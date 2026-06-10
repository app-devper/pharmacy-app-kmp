package app.devper.pharm.data.repository.internal

import app.devper.pharm.common.value.Money
import app.devper.pharm.common.value.Quantity
import app.devper.pharm.data.internal.parseLocalDateOrNull
import app.devper.pharm.data.internal.toIso
import app.devper.pharm.data.remote.dto.DrugLotDto
import app.devper.pharm.data.remote.dto.DrugLotInputDto
import app.devper.pharm.domain.model.DrugLot
import app.devper.pharm.domain.param.inventory.AddLotParam

internal fun DrugLotDto.toDomain(): DrugLot = DrugLot(
    id = id,
    drugId = drugId,
    drugName = drugName?.takeIf { it.isNotBlank() },
    lotNumber = lotNumber,
    expiryDate = expiryDate.parseLocalDateOrNull(),
    importDate = importDate.parseLocalDateOrNull(),
    costPrice = costPrice?.let(::Money),
    sellPrice = sellPrice?.let(::Money),
    quantity = Quantity(quantity),
    remaining = Quantity(remaining),
)

internal fun AddLotParam.toRequest(): DrugLotInputDto = DrugLotInputDto(
    lotNumber = lotNumber,
    expiryDate = expiryDate.toIso(),
    importDate = importDate?.toIso(),
    costPrice = costPrice?.amount,
    sellPrice = sellPrice?.amount,
    quantity = quantity.value,
)
