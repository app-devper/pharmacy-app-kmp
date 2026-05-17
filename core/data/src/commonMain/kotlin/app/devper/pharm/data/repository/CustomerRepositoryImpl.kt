package app.devper.pharm.data.repository

import app.devper.pharm.data.remote.api.CustomerApi
import app.devper.pharm.data.remote.dto.CustomerDto
import app.devper.pharm.data.remote.dto.CustomerInputDto
import app.devper.pharm.data.remote.dto.SaleSummaryDto
import app.devper.pharm.domain.model.Customer
import app.devper.pharm.domain.model.SaleSummary
import app.devper.pharm.domain.param.AddCustomerParam
import app.devper.pharm.domain.param.UpdateCustomerParam
import app.devper.pharm.domain.repository.CustomerRepository

class CustomerRepositoryImpl(private val api: CustomerApi) : CustomerRepository {

    override suspend fun list(): List<Customer> = api.list().map(::toDomain)

    override suspend fun add(param: AddCustomerParam): Customer = toDomain(api.add(param.toDto()))

    override suspend fun update(param: UpdateCustomerParam) {
        api.update(param.id, param.toDto())
    }

    override suspend fun getCustomerSales(customerId: String): List<SaleSummary> =
        api.getSales(customerId).map(::toSaleSummary)

    private fun toDomain(d: CustomerDto) = Customer(
        id = d.id,
        name = d.name,
        phone = d.phone?.takeIf { it.isNotBlank() },
        priceTier = d.priceTier?.takeIf { it.isNotBlank() } ?: "",
        allergyNote = d.disease?.takeIf { it.isNotBlank() },
    )

    private fun toSaleSummary(d: SaleSummaryDto) = SaleSummary(
        id = d.id,
        billNo = d.billNo ?: "",
        customerName = d.customerName,
        total = d.total,
        discount = d.discount,
        soldAt = d.soldAt,
        voided = d.voided,
    )

    private fun AddCustomerParam.toDto() = CustomerInputDto(
        name = name.trim(),
        phone = phone.trim(),
        disease = allergyNote.trim(),
        priceTier = priceTier.trim(),
    )

    private fun UpdateCustomerParam.toDto() = CustomerInputDto(
        name = name.trim(),
        phone = phone.trim(),
        disease = allergyNote.trim(),
        priceTier = priceTier.trim(),
    )
}
