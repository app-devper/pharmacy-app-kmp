package app.devper.pharm.data.repository.internal

import app.devper.pharm.data.remote.dto.SupplierDto
import app.devper.pharm.domain.model.Supplier

internal fun SupplierDto.toDomain(): Supplier = Supplier(
    id = id,
    name = name,
    contactName = contactName,
    phone = phone,
    address = address,
    taxId = taxId,
    notes = notes,
)
