package app.devper.pharm.domain.repository

import app.devper.pharm.domain.model.Supplier
import app.devper.pharm.domain.param.AddSupplierParam
import app.devper.pharm.domain.param.UpdateSupplierParam

class FakeSupplierRepository(
    private val seed: List<Supplier> = emptyList(),
    private val listThrows: Boolean = false,
    private val addThrowsOn: String? = null,
    private val updateThrowsOn: String? = null,
) : SupplierRepository {

    var lastAdd: AddSupplierParam? = null
        private set
    var lastUpdate: UpdateSupplierParam? = null
        private set
    var lastDelete: String? = null
        private set
    var listCallCount: Int = 0
        private set

    override suspend fun list(): List<Supplier> {
        listCallCount++
        if (listThrows) throw RuntimeException("list failed")
        return seed
    }

    override suspend fun add(param: AddSupplierParam): Supplier {
        if (addThrowsOn != null && param.name == addThrowsOn) {
            throw RuntimeException("backend rejected: $addThrowsOn")
        }
        lastAdd = param
        return Supplier(
            id = "new-${param.name}",
            name = param.name,
            contactName = param.contactName,
            phone = param.phone,
            address = param.address,
            taxId = param.taxId,
            notes = param.notes,
        )
    }

    override suspend fun update(param: UpdateSupplierParam) {
        if (param.id == updateThrowsOn) throw RuntimeException("update failed: ${param.id}")
        lastUpdate = param
    }

    override suspend fun delete(id: String) {
        lastDelete = id
    }
}
