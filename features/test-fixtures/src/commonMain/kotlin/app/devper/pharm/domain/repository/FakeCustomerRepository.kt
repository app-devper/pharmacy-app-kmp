package app.devper.pharm.domain.repository

import app.devper.pharm.domain.model.Customer
import app.devper.pharm.domain.model.SaleSummary
import app.devper.pharm.domain.param.AddCustomerParam
import app.devper.pharm.domain.param.UpdateCustomerParam

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

    var lastAdd: AddCustomerParam? = null
        private set
    var lastUpdate: UpdateCustomerParam? = null
        private set
    var lastSalesQuery: String? = null
        private set

    override suspend fun list(): List<Customer> {
        if (listThrows) throw RuntimeException("list failed")
        return seed
    }

    override suspend fun add(param: AddCustomerParam): Customer {
        if (param.name == addThrowsOn) throw RuntimeException("backend rejected: $addThrowsOn")
        lastAdd = param
        return addResult.copy(name = param.name, phone = param.phone, priceTier = param.priceTier)
    }

    override suspend fun update(param: UpdateCustomerParam) {
        if (param.name == updateThrowsOn) throw RuntimeException("backend rejected: $updateThrowsOn")
        lastUpdate = param
    }

    override suspend fun getCustomerSales(customerId: String): List<SaleSummary> {
        lastSalesQuery = customerId
        if (salesThrowsOn != null && customerId == salesThrowsOn) {
            throw RuntimeException("sales failed for $customerId")
        }
        return salesBy[customerId].orEmpty()
    }
}
