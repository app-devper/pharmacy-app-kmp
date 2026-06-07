package app.devper.pharm.data.repository.internal

import app.devper.pharm.data.remote.dto.CustomerDto
import app.devper.pharm.domain.model.Customer

internal fun CustomerDto.toDomain(): Customer = Customer(
    id = id,
    name = name,
    phone = phone?.takeIf { it.isNotBlank() },
    priceTier = priceTier?.takeIf { it.isNotBlank() } ?: "",
    allergyNote = disease?.takeIf { it.isNotBlank() },
)
