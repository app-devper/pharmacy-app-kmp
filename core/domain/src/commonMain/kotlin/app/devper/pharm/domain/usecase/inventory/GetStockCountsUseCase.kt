package app.devper.pharm.domain.usecase

import app.devper.pharm.common.AppDispatchers
import app.devper.pharm.domain.model.StockCount
import app.devper.pharm.domain.repository.StockCountsRepository

class GetStockCountsUseCase(private val repo: StockCountsRepository, dispatchers: AppDispatchers) :
    BaseUseCase<Int, List<StockCount>>(dispatchers) {
    override suspend fun execute(param: Int): List<StockCount> = repo.list(param)
    suspend operator fun invoke(): Result<List<StockCount>> = invoke(20)
}
