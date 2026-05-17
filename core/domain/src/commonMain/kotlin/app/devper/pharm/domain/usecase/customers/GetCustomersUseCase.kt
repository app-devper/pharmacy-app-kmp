package app.devper.pharm.domain.usecase

import app.devper.pharm.common.AppDispatchers
import app.devper.pharm.domain.model.Customer
import app.devper.pharm.domain.repository.CustomerRepository

class GetCustomersUseCase(private val customers: CustomerRepository, dispatchers: AppDispatchers) : BaseUseCase<Unit, List<Customer>>(dispatchers) {
    override suspend fun execute(param: Unit): List<Customer> = customers.list()
    suspend operator fun invoke(): Result<List<Customer>> = invoke(Unit)
}
