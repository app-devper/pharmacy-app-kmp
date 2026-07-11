package app.devper.pharm.domain.repository

import app.devper.pharm.common.ServerException

import app.devper.pharm.domain.repository.suppliers.SupplierRepository

import app.devper.pharm.domain.model.Supplier
import app.devper.pharm.domain.param.suppliers.SupplierInput

class FakeSupplierRepository(
    private val seed: List<Supplier> = emptyList(),
    private val listThrows: Boolean = false,
    private val addThrowsOn: String? = null,
    private val updateThrowsOn: String? = null,
    private val deleteThrows: Boolean = false,
) : SupplierRepository {

    var lastAdd: SupplierInput? = null
        private set
    var lastUpdateId: String? = null
        private set
    var lastUpdate: SupplierInput? = null
        private set
    var lastDelete: String? = null
        private set
    var listCallCount: Int = 0
        private set

    override suspend fun list(): List<Supplier> {
        listCallCount++
        if (listThrows) throw ServerException("list failed")
        return seed
    }

    override suspend fun add(input: SupplierInput): Supplier {
        if (addThrowsOn != null && input.name == addThrowsOn) {
            throw ServerException("backend rejected: $addThrowsOn")
        }
        lastAdd = input
        return Supplier(
            id = "new-${input.name}",
            name = input.name,
            contactName = input.contactName,
            phone = input.phone,
            address = input.address,
            taxId = input.taxId,
            notes = input.notes,
        )
    }

    override suspend fun update(id: String, input: SupplierInput) {
        if (id == updateThrowsOn) throw ServerException("update failed: $id")
        lastUpdateId = id
        lastUpdate = input
    }

    override suspend fun delete(id: String) {
        if (deleteThrows) throw ServerException("delete failed")
        lastDelete = id
    }
}
