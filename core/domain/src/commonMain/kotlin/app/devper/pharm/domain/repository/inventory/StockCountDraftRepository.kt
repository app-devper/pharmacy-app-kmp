package app.devper.pharm.domain.repository.inventory

import app.devper.pharm.domain.model.StockCountDraft

interface StockCountDraftRepository {

    fun load(): StockCountDraft

    fun save(draft: StockCountDraft)

    fun clear()
}
