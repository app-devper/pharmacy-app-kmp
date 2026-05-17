package app.devper.pharm.data.repository

import app.devper.pharm.data.remote.api.SupplierApi
import app.devper.pharm.data.remote.dto.SupplierDto
import app.devper.pharm.data.remote.dto.SupplierInputDto
import app.devper.pharm.domain.model.Supplier
import app.devper.pharm.domain.param.AddSupplierParam
import app.devper.pharm.domain.param.UpdateSupplierParam
import app.devper.pharm.domain.repository.SupplierRepository

class SupplierRepositoryImpl(private val api: SupplierApi) : SupplierRepository {

    override suspend fun list(): List<Supplier> = api.list().map(::toDomain)

    override suspend fun add(param: AddSupplierParam): Supplier = toDomain(api.add(param.toDto()))

    override suspend fun update(param: UpdateSupplierParam) {
        api.update(param.id, param.toDto())
    }

    override suspend fun delete(id: String) {
        api.delete(id)
    }

    private fun toDomain(d: SupplierDto) = Supplier(
        id = d.id,
        name = d.name,
        contactName = d.contactName,
        phone = d.phone,
        address = d.address,
        taxId = d.taxId,
        notes = d.notes,
    )

    private fun AddSupplierParam.toDto() = SupplierInputDto(
        name = name.trim(),
        contactName = contactName.trim(),
        phone = phone.trim(),
        address = address.trim(),
        taxId = taxId.trim(),
        notes = notes.trim(),
    )

    private fun UpdateSupplierParam.toDto() = SupplierInputDto(
        name = name.trim(),
        contactName = contactName.trim(),
        phone = phone.trim(),
        address = address.trim(),
        taxId = taxId.trim(),
        notes = notes.trim(),
    )
}
