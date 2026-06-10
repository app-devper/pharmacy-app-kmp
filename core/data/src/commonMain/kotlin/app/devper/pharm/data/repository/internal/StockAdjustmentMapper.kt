package app.devper.pharm.data.repository.internal

import app.devper.pharm.data.remote.dto.StockAdjustmentDto
import app.devper.pharm.data.remote.dto.StockAdjustmentInputDto
import app.devper.pharm.domain.model.AdjustmentReason
import app.devper.pharm.domain.model.StockAdjustment
import app.devper.pharm.domain.param.inventory.AddStockAdjustmentParam

internal fun StockAdjustmentDto.toDomain(): StockAdjustment = StockAdjustment(
    id = id,
    drugId = drugId,
    drugName = drugName,
    delta = delta,
    before = before,
    after = after,
    reason = AdjustmentReason.fromWire(reason),
    note = note,
    at = createdAt,
)

internal fun AddStockAdjustmentParam.toDto(): StockAdjustmentInputDto = StockAdjustmentInputDto(
    delta = delta,
    reason = reason.wire,
    note = note.trim(),
)
