package app.devper.pharm.data.repository

import app.devper.pharm.data.internal.toIso
import app.devper.pharm.data.remote.api.SaleHistoryApi
import app.devper.pharm.data.remote.dto.DrugReturnItemRequest
import app.devper.pharm.data.remote.dto.DrugReturnRequest
import app.devper.pharm.data.repository.internal.toDomain
import app.devper.pharm.domain.event.StockChangeBus
import app.devper.pharm.domain.model.SaleItemSnapshot
import app.devper.pharm.domain.model.SaleSummary
import app.devper.pharm.domain.param.sales.SaleHistoryFilterParam
import app.devper.pharm.domain.param.sales.SubmitReturnParam
import app.devper.pharm.domain.repository.sales.SaleHistoryRepository

class SaleHistoryRepositoryImpl(
    private val api: SaleHistoryApi,
    private val stockChangeBus: StockChangeBus,
) : SaleHistoryRepository {

    override suspend fun list(filter: SaleHistoryFilterParam): List<SaleSummary> =
        api.list(filter.from?.toIso(), filter.to?.toIso(), filter.query, filter.limit).map { it.toDomain() }

    override suspend fun getItems(saleId: String): List<SaleItemSnapshot> =
        api.items(saleId).map { it.toDomain() }

    override suspend fun submitReturn(param: SubmitReturnParam) {
        api.submitReturn(
            saleId = param.saleId,
            request = DrugReturnRequest(
                reason = param.reason,
                items = param.items.map { DrugReturnItemRequest(it.saleItemId, it.qty) },
                clientRequestId = param.clientRequestId,
            ),
        )
        stockChangeBus.emit()
    }
}
