package app.devper.pharm.data.repository.internal

import app.devper.pharm.data.remote.dto.SupplierDto
import app.devper.pharm.data.remote.dto.SupplierInputDto
import app.devper.pharm.domain.model.Supplier
import app.devper.pharm.domain.param.suppliers.SupplierInput

internal fun SupplierDto.toDomain(): Supplier = Supplier(
    id = id,
    name = name,
    contactName = contactName,
    phone = phone,
    address = address,
    taxId = taxId,
    notes = notes,
)

internal fun SupplierInput.toDto(): SupplierInputDto = SupplierInputDto(
    name = name.trim(),
    contactName = contactName.trim(),
    phone = phone.trim(),
    address = address.trim(),
    taxId = taxId.trim(),
    notes = notes.trim(),
)
