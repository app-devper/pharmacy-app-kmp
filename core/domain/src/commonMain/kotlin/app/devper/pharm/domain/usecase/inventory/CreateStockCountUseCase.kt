package app.devper.pharm.domain.usecase.inventory

import app.devper.pharm.domain.usecase.BaseUseCase

import app.devper.pharm.common.AppDispatchers
import app.devper.pharm.domain.model.StockCount
import app.devper.pharm.domain.param.inventory.CreateStockCountParam
import app.devper.pharm.domain.repository.inventory.StockCountsRepository

class CreateStockCountUseCase(private val repo: StockCountsRepository, dispatchers: AppDispatchers) :
    BaseUseCase<CreateStockCountParam, StockCount>(dispatchers) {
    override suspend fun execute(param: CreateStockCountParam): StockCount = repo.add(param)
}
