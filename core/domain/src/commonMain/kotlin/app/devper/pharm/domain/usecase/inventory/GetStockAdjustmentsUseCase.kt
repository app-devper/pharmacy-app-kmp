package app.devper.pharm.domain.usecase

import app.devper.pharm.common.AppDispatchers
import app.devper.pharm.domain.model.StockAdjustment
import app.devper.pharm.domain.repository.StockAdjustmentsRepository

class GetStockAdjustmentsUseCase(private val repo: StockAdjustmentsRepository, dispatchers: AppDispatchers) :
    BaseUseCase<String, List<StockAdjustment>>(dispatchers) {
    override suspend fun execute(param: String): List<StockAdjustment> = repo.list(param)
}
