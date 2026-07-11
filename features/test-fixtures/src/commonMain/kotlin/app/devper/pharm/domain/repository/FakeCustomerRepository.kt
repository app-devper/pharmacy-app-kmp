package app.devper.pharm.domain.repository

import app.devper.pharm.common.ServerException

import app.devper.pharm.domain.repository.customers.CustomerRepository

import app.devper.pharm.domain.model.Customer
import app.devper.pharm.domain.model.SaleSummary
import app.devper.pharm.domain.param.customers.CustomerInput

class FakeCustomerRepository(
    private val seed: List<Customer> = emptyList(),
    private val addResult: Customer = Customer(
        id = "new-id",
        name = "",
        phone = null,
        priceTier = "",
        allergyNote = null,
    ),
    private val addThrowsOn: String? = null,
    private val updateThrowsOn: String? = null,
    private val listThrows: Boolean = false,
    private val salesBy: Map<String, List<SaleSummary>> = emptyMap(),
    private val salesThrowsOn: String? = null,
) : CustomerRepository {

    var lastAdd: CustomerInput? = null
        private set
    var lastUpdateId: String? = null
        private set
    var lastUpdate: CustomerInput? = null
        private set
    var lastSalesQuery: String? = null
        private set

    override suspend fun list(): List<Customer> {
        if (listThrows) throw ServerException("list failed")
        return seed
    }

    override suspend fun add(input: CustomerInput): Customer {
        if (input.name == addThrowsOn) throw ServerException("backend rejected: $addThrowsOn")
        lastAdd = input
        return addResult.copy(name = input.name, phone = input.phone, priceTier = input.priceTier)
    }

    override suspend fun update(id: String, input: CustomerInput) {
        if (input.name == updateThrowsOn) throw ServerException("backend rejected: $updateThrowsOn")
        lastUpdateId = id
        lastUpdate = input
    }

    override suspend fun getCustomerSales(customerId: String): List<SaleSummary> {
        lastSalesQuery = customerId
        if (salesThrowsOn != null && customerId == salesThrowsOn) {
            throw ServerException("sales failed for $customerId")
        }
        return salesBy[customerId].orEmpty()
    }
}
