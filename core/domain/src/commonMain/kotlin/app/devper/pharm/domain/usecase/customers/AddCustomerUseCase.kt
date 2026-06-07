package app.devper.pharm.domain.usecase

import app.devper.pharm.common.AppDispatchers
import app.devper.pharm.domain.model.Customer
import app.devper.pharm.domain.param.CustomerInput
import app.devper.pharm.domain.repository.CustomerRepository

class AddCustomerUseCase(private val customers: CustomerRepository, dispatchers: AppDispatchers) :
    BaseUseCase<CustomerInput, Customer>(dispatchers) {
    override suspend fun execute(param: CustomerInput): Customer = customers.add(param)
}
