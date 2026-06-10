package app.devper.pharm.domain.repository.inventory

import app.devper.pharm.domain.model.StockAdjustment
import app.devper.pharm.domain.param.inventory.AddStockAdjustmentParam

interface StockAdjustmentsRepository {

    suspend fun list(drugId: String): List<StockAdjustment>

    suspend fun add(param: AddStockAdjustmentParam)
}
