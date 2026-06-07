package app.devper.pharm.domain.usecase

import app.devper.pharm.common.AppDispatchers
import app.devper.pharm.domain.repository.StockCountDraftRepository

class ClearStockCountDraftUseCase(
    private val draftRepo: StockCountDraftRepository,
    dispatchers: AppDispatchers,
) : BaseQueryUseCase<Unit>(dispatchers) {
    override suspend fun execute(param: Unit) = draftRepo.clear()
}
