package app.devper.pharm.domain.repository

import app.devper.pharm.domain.model.SaleItemSnapshot
import app.devper.pharm.domain.model.SaleSummary
import app.devper.pharm.domain.param.SaleHistoryFilterParam
import app.devper.pharm.domain.param.SubmitReturnParam

class FakeSaleHistoryRepository(
    private val seed: List<SaleSummary> = emptyList(),
    private val itemsBySale: Map<String, List<SaleItemSnapshot>> = emptyMap(),
    private val returnsBySale: Map<String, Map<String, Int>> = emptyMap(),
    private val listThrows: Boolean = false,
    private val itemsThrows: Boolean = false,
    private val returnsThrows: Boolean = false,
    private val submitThrowsOn: String? = null,
) : SaleHistoryRepository {

    var lastListFilter: SaleHistoryFilterParam? = null
        private set
    var lastSubmitReturn: SubmitReturnParam? = null
        private set
    var listCallCount: Int = 0
        private set
    var itemsCallCount: Int = 0
        private set

    override suspend fun list(filter: SaleHistoryFilterParam): List<SaleSummary> {
        lastListFilter = filter
        listCallCount++
        if (listThrows) throw RuntimeException("list failed")
        return seed.filter { sale ->
            (filter.query.isNullOrBlank() || sale.billNo.contains(filter.query!!, ignoreCase = true))
        }
    }

    override suspend fun getItems(saleId: String): List<SaleItemSnapshot> {
        itemsCallCount++
        if (itemsThrows) throw RuntimeException("items failed")
        return itemsBySale[saleId].orEmpty()
    }

    override suspend fun getReturnedQuantities(saleId: String): Map<String, Int> {
        if (returnsThrows) throw RuntimeException("returns failed")
        return returnsBySale[saleId].orEmpty()
    }

    override suspend fun submitReturn(param: SubmitReturnParam) {
        if (submitThrowsOn != null && param.saleId == submitThrowsOn) {
            throw RuntimeException("submit failed for ${param.saleId}")
        }
        lastSubmitReturn = param
    }
}
