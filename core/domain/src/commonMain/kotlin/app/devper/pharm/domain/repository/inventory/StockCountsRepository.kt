package app.devper.pharm.domain.repository.inventory

import app.devper.pharm.domain.model.StockCount
import app.devper.pharm.domain.param.inventory.CreateStockCountParam

interface StockCountsRepository {

    suspend fun list(limit: Int = 20): List<StockCount>

    suspend fun add(param: CreateStockCountParam): StockCount
}
