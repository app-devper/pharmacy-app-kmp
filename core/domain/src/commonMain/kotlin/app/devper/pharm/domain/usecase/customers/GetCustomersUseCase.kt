package app.devper.pharm.domain.usecase

import app.devper.pharm.common.AppDispatchers
import app.devper.pharm.domain.model.Customer
import app.devper.pharm.domain.repository.CustomerRepository

class GetCustomersUseCase(private val customers: CustomerRepository, dispatchers: AppDispatchers) : BaseQueryUseCase<List<Customer>>(dispatchers) {
    override suspend fun execute(param: Unit): List<Customer> = customers.list()
}
