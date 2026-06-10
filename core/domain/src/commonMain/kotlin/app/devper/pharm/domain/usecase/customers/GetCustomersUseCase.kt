package app.devper.pharm.domain.usecase.customers

import app.devper.pharm.domain.usecase.BaseQueryUseCase

import app.devper.pharm.common.AppDispatchers
import app.devper.pharm.domain.model.Customer
import app.devper.pharm.domain.repository.customers.CustomerRepository

class GetCustomersUseCase(private val customers: CustomerRepository, dispatchers: AppDispatchers) : BaseQueryUseCase<List<Customer>>(dispatchers) {
    override suspend fun execute(param: Unit): List<Customer> = customers.list()
}
