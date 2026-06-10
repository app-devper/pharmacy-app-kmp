package app.devper.pharm.domain.usecase.customers

import app.devper.pharm.domain.usecase.BaseUseCase

import app.devper.pharm.common.AppDispatchers
import app.devper.pharm.domain.model.SaleSummary
import app.devper.pharm.domain.repository.customers.CustomerRepository

class GetCustomerSalesUseCase(private val customers: CustomerRepository, dispatchers: AppDispatchers) :
    BaseUseCase<String, List<SaleSummary>>(dispatchers) {
    override suspend fun execute(param: String): List<SaleSummary> = customers.getCustomerSales(param)
}
