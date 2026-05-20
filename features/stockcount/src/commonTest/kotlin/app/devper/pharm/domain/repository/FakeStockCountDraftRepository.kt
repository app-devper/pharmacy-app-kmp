package app.devper.pharm.domain.repository

import app.devper.pharm.domain.model.StockCountDraft

class FakeStockCountDraftRepository(
    initial: StockCountDraft = StockCountDraft.Empty,
) : StockCountDraftRepository {

    var stored: StockCountDraft = initial
        private set

    var loadCallCount: Int = 0
        private set
    var saveCallCount: Int = 0
        private set
    var clearCallCount: Int = 0
        private set

    override fun load(): StockCountDraft {
        loadCallCount++
        return stored
    }

    override fun save(draft: StockCountDraft) {
        saveCallCount++
        stored = draft
    }

    override fun clear() {
        clearCallCount++
        stored = StockCountDraft.Empty
    }
}
