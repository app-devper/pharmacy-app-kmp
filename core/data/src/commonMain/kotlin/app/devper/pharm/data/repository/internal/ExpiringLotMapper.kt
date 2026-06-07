package app.devper.pharm.data.repository.internal

import app.devper.pharm.data.internal.parseLocalDateOrNull
import app.devper.pharm.data.remote.dto.ExpiringLotDto
import app.devper.pharm.data.remote.dto.WriteoffFailureDto
import app.devper.pharm.data.remote.dto.WriteoffResultDto
import app.devper.pharm.domain.model.ExpiringLot
import app.devper.pharm.domain.model.WriteoffFailure
import app.devper.pharm.domain.model.WriteoffResult

internal fun ExpiringLotDto.toDomain(): ExpiringLot = ExpiringLot(
    id = id,
    drugId = drugId,
    drugName = drugName,
    lotNumber = lotNumber,
    expiryDate = expiryDate.parseLocalDateOrNull(),
    remaining = remaining,
    daysLeft = daysLeft,
)

internal fun WriteoffResultDto.toDomain(): WriteoffResult = WriteoffResult(
    writtenOff = writtenOff,
    failures = failed.map { it.toDomain() },
)

internal fun WriteoffFailureDto.toDomain(): WriteoffFailure = WriteoffFailure(
    lotId = lotId,
    message = error,
)
