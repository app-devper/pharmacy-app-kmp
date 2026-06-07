package app.devper.pharm.data.repository.internal

import app.devper.pharm.data.remote.dto.StockAdjustmentDto
import app.devper.pharm.domain.model.AdjustmentReason
import app.devper.pharm.domain.model.StockAdjustment

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
