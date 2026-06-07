package app.devper.pharm.data.repository

import app.devper.pharm.data.remote.api.SupplierApi
import app.devper.pharm.data.repository.internal.toDomain
import app.devper.pharm.data.repository.internal.toDto
import app.devper.pharm.domain.model.Supplier
import app.devper.pharm.domain.param.AddSupplierParam
import app.devper.pharm.domain.param.UpdateSupplierParam
import app.devper.pharm.domain.repository.SupplierRepository

class SupplierRepositoryImpl(private val api: SupplierApi) : SupplierRepository {

    override suspend fun list(): List<Supplier> = api.list().map { it.toDomain() }

    override suspend fun add(param: AddSupplierParam): Supplier = api.add(param.toDto()).toDomain()

    override suspend fun update(param: UpdateSupplierParam) {
        api.update(param.id, param.toDto())
    }

    override suspend fun delete(id: String) {
        api.delete(id)
    }
}
