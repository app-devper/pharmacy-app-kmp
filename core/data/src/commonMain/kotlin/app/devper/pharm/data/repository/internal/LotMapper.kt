package app.devper.pharm.data.repository.internal

import app.devper.pharm.data.internal.parseLocalDateOrNull
import app.devper.pharm.data.internal.toIso
import app.devper.pharm.data.remote.dto.DrugLotDto
import app.devper.pharm.data.remote.dto.DrugLotInputDto
import app.devper.pharm.domain.model.DrugLot
import app.devper.pharm.domain.param.AddLotParam

internal fun DrugLotDto.toDomain(): DrugLot = DrugLot(
    id = id,
    drugId = drugId,
    drugName = drugName?.takeIf { it.isNotBlank() },
    lotNumber = lotNumber,
    expiryDate = expiryDate.parseLocalDateOrNull(),
    importDate = importDate.parseLocalDateOrNull(),
    costPrice = costPrice,
    sellPrice = sellPrice,
    quantity = quantity,
    remaining = remaining,
)

internal fun AddLotParam.toRequest(): DrugLotInputDto = DrugLotInputDto(
    lotNumber = lotNumber,
    expiryDate = expiryDate.toIso(),
    importDate = importDate?.toIso(),
    costPrice = costPrice,
    sellPrice = sellPrice,
    quantity = quantity,
)
