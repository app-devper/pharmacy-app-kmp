package app.devper.pharm.domain.repository

import app.devper.pharm.domain.model.StockCount
import app.devper.pharm.domain.param.CreateStockCountParam

interface StockCountsRepository {

    suspend fun list(limit: Int = 20): List<StockCount>

    suspend fun add(param: CreateStockCountParam): StockCount
}
