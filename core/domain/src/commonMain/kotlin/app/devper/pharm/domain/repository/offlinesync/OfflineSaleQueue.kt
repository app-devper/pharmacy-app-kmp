package app.devper.pharm.domain.repository.offlinesync

import app.devper.pharm.domain.model.PendingSale
import kotlinx.coroutines.flow.StateFlow

interface OfflineSaleQueue {
    val entries: StateFlow<List<PendingSale>>

    fun put(entry: PendingSale)

    fun remove(id: String)
}
