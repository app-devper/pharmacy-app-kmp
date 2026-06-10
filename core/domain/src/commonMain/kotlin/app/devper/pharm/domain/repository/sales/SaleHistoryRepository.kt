package app.devper.pharm.domain.repository.sales

import app.devper.pharm.domain.model.SaleItemSnapshot
import app.devper.pharm.domain.model.SaleSummary
import app.devper.pharm.domain.param.sales.SaleHistoryFilterParam
import app.devper.pharm.domain.param.sales.SubmitReturnParam

interface SaleHistoryRepository {
    suspend fun list(filter: SaleHistoryFilterParam): List<SaleSummary>
    suspend fun getItems(saleId: String): List<SaleItemSnapshot>
    suspend fun getReturnedQuantities(saleId: String): Map<String, Int>

    suspend fun submitReturn(param: SubmitReturnParam)
}
