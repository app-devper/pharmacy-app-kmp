package app.devper.pharm.data.repository

import app.devper.pharm.data.remote.api.SupplierApi
import app.devper.pharm.data.repository.internal.toDomain
import app.devper.pharm.data.repository.internal.toDto
import app.devper.pharm.domain.model.Supplier
import app.devper.pharm.domain.param.suppliers.SupplierInput
import app.devper.pharm.domain.repository.suppliers.SupplierRepository

class SupplierRepositoryImpl(private val api: SupplierApi) : SupplierRepository {

    override suspend fun list(): List<Supplier> = api.list().map { it.toDomain() }

    override suspend fun add(input: SupplierInput): Supplier = api.add(input.toDto()).toDomain()

    override suspend fun update(id: String, input: SupplierInput) {
        api.update(id, input.toDto())
    }

    override suspend fun delete(id: String) {
        api.delete(id)
    }
}
