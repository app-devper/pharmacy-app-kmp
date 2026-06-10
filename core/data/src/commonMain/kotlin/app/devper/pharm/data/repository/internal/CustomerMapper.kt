package app.devper.pharm.data.repository.internal

import app.devper.pharm.data.remote.dto.CustomerDto
import app.devper.pharm.data.remote.dto.CustomerInputDto
import app.devper.pharm.domain.model.Customer
import app.devper.pharm.domain.param.customers.CustomerInput

internal fun CustomerDto.toDomain(): Customer = Customer(
    id = id,
    name = name,
    phone = phone?.takeIf { it.isNotBlank() },
    priceTier = priceTier?.takeIf { it.isNotBlank() } ?: "",
    allergyNote = disease?.takeIf { it.isNotBlank() },
)

internal fun CustomerInput.toDto(): CustomerInputDto = CustomerInputDto(
    name = name.trim(),
    phone = phone.trim(),
    disease = allergyNote.trim(),
    priceTier = priceTier.trim(),
)
