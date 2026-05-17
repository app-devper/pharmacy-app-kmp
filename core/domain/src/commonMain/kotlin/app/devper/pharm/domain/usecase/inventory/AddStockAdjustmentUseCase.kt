package app.devper.pharm.domain.usecase

import app.devper.pharm.common.AppDispatchers
import app.devper.pharm.domain.param.AddStockAdjustmentParam
import app.devper.pharm.domain.repository.StockAdjustmentsRepository

class AddStockAdjustmentUseCase(private val repo: StockAdjustmentsRepository, dispatchers: AppDispatchers) :
    BaseUseCase<AddStockAdjustmentParam, Unit>(dispatchers) {
    override suspend fun execute(param: AddStockAdjustmentParam) = repo.add(param)
}
