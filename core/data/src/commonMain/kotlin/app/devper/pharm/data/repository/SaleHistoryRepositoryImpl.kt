package app.devper.pharm.data.repository

import app.devper.pharm.common.NotFoundException
import app.devper.pharm.data.remote.api.SaleHistoryApi
import app.devper.pharm.data.remote.dto.DrugReturnItemRequest
import app.devper.pharm.data.remote.dto.DrugReturnRequest
import app.devper.pharm.data.remote.dto.SaleItemDto
import app.devper.pharm.data.remote.dto.SaleSummaryDto
import app.devper.pharm.domain.event.StockChangeBus
import app.devper.pharm.domain.model.SaleItemSnapshot
import app.devper.pharm.domain.model.SaleSummary
import app.devper.pharm.domain.param.SaleHistoryFilterParam
import app.devper.pharm.domain.param.SubmitReturnParam
import app.devper.pharm.domain.repository.SaleHistoryRepository

class SaleHistoryRepositoryImpl(
    private val api: SaleHistoryApi,
    private val stockChangeBus: StockChangeBus,
) : SaleHistoryRepository {

    override suspend fun list(filter: SaleHistoryFilterParam): List<SaleSummary> =
        api.list(filter.from, filter.to, filter.query, filter.limit).map { it.toDomain() }

    override suspend fun get(saleId: String): SaleSummary {

        val matched = api.list(null, null, null, 200).firstOrNull { it.id == saleId }
            ?: throw NotFoundException("ไม่พบบิล")
        return matched.toDomain()
    }

    override suspend fun getItems(saleId: String): List<SaleItemSnapshot> =
        api.items(saleId).map { it.toDomain(returnedQty = 0) }

    override suspend fun getReturnedQuantities(saleId: String): Map<String, Int> {
        val returns = api.returns(saleId)
        val totals = mutableMapOf<String, Int>()
        for (ret in returns) {
            for (line in ret.items) {
                totals[line.saleItemId] = (totals[line.saleItemId] ?: 0) + line.qty
            }
        }
        return totals
    }

    override suspend fun submitReturn(param: SubmitReturnParam) {
        api.submitReturn(
            saleId = param.saleId,
            request = DrugReturnRequest(
                reason = param.reason,
                items = param.items.map { DrugReturnItemRequest(it.saleItemId, it.qty) },
            ),
        )
        stockChangeBus.emit()
    }

    private fun SaleSummaryDto.toDomain() = SaleSummary(
        id = id,
        billNo = billNo ?: "",
        customerName = customerName,
        total = total,
        discount = discount,
        soldAt = soldAt,
        voided = voided,
    )

    private fun SaleItemDto.toDomain(returnedQty: Int) = SaleItemSnapshot(
        id = id,
        drugId = drugId,
        drugName = drugName,
        qty = qty,
        price = price,
        originalPrice = originalPrice,
        itemDiscount = itemDiscount,
        unit = unit,
        unitFactor = unitFactor,
        priceTier = priceTier,
        returnedQty = returnedQty,
    )
}
