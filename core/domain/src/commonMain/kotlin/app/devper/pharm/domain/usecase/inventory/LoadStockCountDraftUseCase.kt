package app.devper.pharm.domain.usecase

import app.devper.pharm.domain.model.StockCountDraft
import app.devper.pharm.domain.repository.StockCountDraftRepository

class LoadStockCountDraftUseCase(
    private val draftRepo: StockCountDraftRepository,
) : BaseSyncUseCase<Unit, StockCountDraft>() {
    override fun execute(param: Unit): StockCountDraft = draftRepo.load()
}
