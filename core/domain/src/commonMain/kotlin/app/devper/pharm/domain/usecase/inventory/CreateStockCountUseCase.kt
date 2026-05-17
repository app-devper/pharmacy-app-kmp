package app.devper.pharm.domain.usecase

import app.devper.pharm.common.AppDispatchers
import app.devper.pharm.domain.model.StockCount
import app.devper.pharm.domain.param.CreateStockCountParam
import app.devper.pharm.domain.repository.StockCountsRepository

class CreateStockCountUseCase(private val repo: StockCountsRepository, dispatchers: AppDispatchers) :
    BaseUseCase<CreateStockCountParam, StockCount>(dispatchers) {
    override suspend fun execute(param: CreateStockCountParam): StockCount = repo.add(param)
}
