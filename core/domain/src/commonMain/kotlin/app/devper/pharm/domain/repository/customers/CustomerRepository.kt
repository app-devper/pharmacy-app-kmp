package app.devper.pharm.domain.repository

import app.devper.pharm.domain.model.Customer
import app.devper.pharm.domain.model.SaleSummary
import app.devper.pharm.domain.param.AddCustomerParam
import app.devper.pharm.domain.param.UpdateCustomerParam

interface CustomerRepository {
    suspend fun list(): List<Customer>
    suspend fun add(param: AddCustomerParam): Customer
    suspend fun update(param: UpdateCustomerParam)
    suspend fun getCustomerSales(customerId: String): List<SaleSummary>
}
