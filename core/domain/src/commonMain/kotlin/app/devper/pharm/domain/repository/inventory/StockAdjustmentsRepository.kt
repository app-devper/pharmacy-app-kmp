package app.devper.pharm.domain.repository

import app.devper.pharm.domain.model.StockAdjustment
import app.devper.pharm.domain.param.AddStockAdjustmentParam

interface StockAdjustmentsRepository {

    suspend fun list(drugId: String): List<StockAdjustment>

    suspend fun add(param: AddStockAdjustmentParam)
}
