package app.devper.pharm.data.repository

import app.devper.pharm.data.remote.api.StockAdjustmentApi
import app.devper.pharm.data.repository.internal.toDomain
import app.devper.pharm.data.repository.internal.toDto
import app.devper.pharm.domain.event.StockChangeBus
import app.devper.pharm.domain.model.StockAdjustment
import app.devper.pharm.domain.param.AddStockAdjustmentParam
import app.devper.pharm.domain.repository.StockAdjustmentsRepository

class StockAdjustmentsRepositoryImpl(
    private val api: StockAdjustmentApi,
    private val stockChangeBus: StockChangeBus,
) : StockAdjustmentsRepository {

    override suspend fun list(drugId: String): List<StockAdjustment> =
        api.list(drugId).map { it.toDomain() }

    override suspend fun add(param: AddStockAdjustmentParam) {

        api.add(param.drugId, param.toDto())
        stockChangeBus.emit()
    }
}
