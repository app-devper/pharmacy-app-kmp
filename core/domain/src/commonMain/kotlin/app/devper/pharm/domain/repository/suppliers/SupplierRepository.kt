package app.devper.pharm.domain.repository.suppliers

import app.devper.pharm.domain.model.Supplier
import app.devper.pharm.domain.param.suppliers.SupplierInput

interface SupplierRepository {
    suspend fun list(): List<Supplier>
    suspend fun add(input: SupplierInput): Supplier
    suspend fun update(id: String, input: SupplierInput)
    suspend fun delete(id: String)
}
