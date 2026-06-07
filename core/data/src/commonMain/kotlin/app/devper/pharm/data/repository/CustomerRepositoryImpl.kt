package app.devper.pharm.data.repository

import app.devper.pharm.data.remote.api.CustomerApi
import app.devper.pharm.data.repository.internal.toDomain
import app.devper.pharm.data.repository.internal.toDto
import app.devper.pharm.domain.model.Customer
import app.devper.pharm.domain.model.SaleSummary
import app.devper.pharm.domain.param.AddCustomerParam
import app.devper.pharm.domain.param.UpdateCustomerParam
import app.devper.pharm.domain.repository.CustomerRepository

class CustomerRepositoryImpl(private val api: CustomerApi) : CustomerRepository {

    override suspend fun list(): List<Customer> = api.list().map { it.toDomain() }

    override suspend fun add(param: AddCustomerParam): Customer = api.add(param.toDto()).toDomain()

    override suspend fun update(param: UpdateCustomerParam) {
        api.update(param.id, param.toDto())
    }

    override suspend fun getCustomerSales(customerId: String): List<SaleSummary> =
        api.getSales(customerId).map { it.toDomain() }
}
