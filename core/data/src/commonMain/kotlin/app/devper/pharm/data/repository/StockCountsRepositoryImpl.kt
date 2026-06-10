package app.devper.pharm.data.repository

import app.devper.pharm.data.remote.api.StockCountsApi
import app.devper.pharm.data.repository.internal.toDomain
import app.devper.pharm.data.repository.internal.toDto
import app.devper.pharm.domain.event.StockChangeBus
import app.devper.pharm.domain.model.StockCount
import app.devper.pharm.domain.param.inventory.CreateStockCountParam
import app.devper.pharm.domain.repository.inventory.StockCountsRepository

class StockCountsRepositoryImpl(
    private val api: StockCountsApi,
    private val stockChangeBus: StockChangeBus,
) : StockCountsRepository {

    override suspend fun list(limit: Int): List<StockCount> =
        api.list(limit).map { it.toDomain() }

    override suspend fun add(param: CreateStockCountParam): StockCount {

        val result = api.add(param.toDto()).toDomain()
        stockChangeBus.emit()
        return result
    }
}
