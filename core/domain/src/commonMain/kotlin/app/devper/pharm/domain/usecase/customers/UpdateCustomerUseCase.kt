package app.devper.pharm.domain.usecase.customers

import app.devper.pharm.domain.usecase.BaseUseCase

import app.devper.pharm.common.AppDispatchers
import app.devper.pharm.domain.param.UpdateCustomerParam
import app.devper.pharm.domain.repository.CustomerRepository

class UpdateCustomerUseCase(private val customers: CustomerRepository, dispatchers: AppDispatchers) :
    BaseUseCase<UpdateCustomerParam, Unit>(dispatchers) {
    override suspend fun execute(param: UpdateCustomerParam) = customers.update(param.id, param.input)
}
