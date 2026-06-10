package app.devper.pharm.domain.usecase.customers

import app.devper.pharm.domain.usecase.BaseUseCase

import app.devper.pharm.common.AppDispatchers
import app.devper.pharm.domain.model.Customer
import app.devper.pharm.domain.param.customers.CustomerInput
import app.devper.pharm.domain.repository.customers.CustomerRepository

class AddCustomerUseCase(private val customers: CustomerRepository, dispatchers: AppDispatchers) :
    BaseUseCase<CustomerInput, Customer>(dispatchers) {
    override suspend fun execute(param: CustomerInput): Customer = customers.add(param)
}
