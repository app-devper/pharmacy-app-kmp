package app.devper.pharm.data.repository.internal

import app.devper.pharm.data.remote.dto.MovementDto
import app.devper.pharm.domain.model.MovementType
import app.devper.pharm.domain.model.StockMovement

internal fun MovementDto.toDomainOrNull(): StockMovement? {
    val movementType = MovementType.fromWire(type) ?: return null
    return StockMovement(
        id = id,
        type = movementType,
        drugId = drugId,
        drugName = drugName,
        delta = delta,
        reference = reference,
        note = note,
        at = at,
    )
}
