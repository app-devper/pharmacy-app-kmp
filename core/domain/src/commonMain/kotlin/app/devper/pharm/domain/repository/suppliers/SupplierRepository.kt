package app.devper.pharm.domain.repository

import app.devper.pharm.domain.model.Supplier
import app.devper.pharm.domain.param.AddSupplierParam
import app.devper.pharm.domain.param.UpdateSupplierParam

interface SupplierRepository {
    suspend fun list(): List<Supplier>
    suspend fun add(param: AddSupplierParam): Supplier
    suspend fun update(param: UpdateSupplierParam)
    suspend fun delete(id: String)
}
