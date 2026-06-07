package app.devper.pharm.domain.repository

import app.devper.pharm.domain.model.Customer
import app.devper.pharm.domain.model.SaleSummary
import app.devper.pharm.domain.param.CustomerInput

interface CustomerRepository {
    suspend fun list(): List<Customer>
    suspend fun add(input: CustomerInput): Customer
    suspend fun update(id: String, input: CustomerInput)
    suspend fun getCustomerSales(customerId: String): List<SaleSummary>
}
