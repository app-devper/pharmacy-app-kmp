package app.devper.pharm.data.repository

import app.devper.pharm.data.remote.api.CustomerApi
import app.devper.pharm.data.repository.internal.toDomain
import app.devper.pharm.data.repository.internal.toDto
import app.devper.pharm.domain.model.Customer
import app.devper.pharm.domain.model.SaleSummary
import app.devper.pharm.domain.param.CustomerInput
import app.devper.pharm.domain.repository.CustomerRepository

class CustomerRepositoryImpl(private val api: CustomerApi) : CustomerRepository {

    override suspend fun list(): List<Customer> = api.list().map { it.toDomain() }

    override suspend fun add(input: CustomerInput): Customer = api.add(input.toDto()).toDomain()

    override suspend fun update(id: String, input: CustomerInput) {
        api.update(id, input.toDto())
    }

    override suspend fun getCustomerSales(customerId: String): List<SaleSummary> =
        api.getSales(customerId).map { it.toDomain() }
}
