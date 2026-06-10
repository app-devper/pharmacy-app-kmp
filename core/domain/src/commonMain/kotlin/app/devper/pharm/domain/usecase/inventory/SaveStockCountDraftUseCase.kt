package app.devper.pharm.domain.usecase.inventory

import app.devper.pharm.domain.usecase.BaseUseCase

import app.devper.pharm.common.AppDispatchers
import app.devper.pharm.domain.model.StockCountDraft
import app.devper.pharm.domain.repository.inventory.StockCountDraftRepository

class SaveStockCountDraftUseCase(
    private val draftRepo: StockCountDraftRepository,
    dispatchers: AppDispatchers,
) : BaseUseCase<StockCountDraft, Unit>(dispatchers) {
    override suspend fun execute(param: StockCountDraft) = draftRepo.save(param)
}
