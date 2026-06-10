package app.devper.pharm.domain.usecase.inventory

import app.devper.pharm.domain.usecase.BaseUseCase

import app.devper.pharm.common.AppDispatchers
import app.devper.pharm.domain.param.inventory.AddStockAdjustmentParam
import app.devper.pharm.domain.repository.inventory.StockAdjustmentsRepository

class AddStockAdjustmentUseCase(private val repo: StockAdjustmentsRepository, dispatchers: AppDispatchers) :
    BaseUseCase<AddStockAdjustmentParam, Unit>(dispatchers) {
    override suspend fun execute(param: AddStockAdjustmentParam) = repo.add(param)
}
