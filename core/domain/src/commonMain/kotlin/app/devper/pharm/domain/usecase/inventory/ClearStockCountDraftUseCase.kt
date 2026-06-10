package app.devper.pharm.domain.usecase.inventory

import app.devper.pharm.domain.usecase.BaseQueryUseCase

import app.devper.pharm.common.AppDispatchers
import app.devper.pharm.domain.repository.inventory.StockCountDraftRepository

class ClearStockCountDraftUseCase(
    private val draftRepo: StockCountDraftRepository,
    dispatchers: AppDispatchers,
) : BaseQueryUseCase<Unit>(dispatchers) {
    override suspend fun execute(param: Unit) = draftRepo.clear()
}
