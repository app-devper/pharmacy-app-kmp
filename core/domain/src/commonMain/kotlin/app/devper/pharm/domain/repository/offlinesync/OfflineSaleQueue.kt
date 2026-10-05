package app.devper.pharm.domain.repository.offlinesync

import app.devper.pharm.domain.model.PendingSale
import kotlinx.coroutines.flow.StateFlow

/**
 * Durable storage for pending sales, one entry at a time (KMP ADR-0006): a
 * damaged entry never takes the others with it, and is reported as
 * [app.devper.pharm.domain.model.PendingSaleState.Damaged] with its raw data.
 * [app.devper.pharm.domain.pendingsales.PendingSales] owns what the states mean.
 */
interface OfflineSaleQueue {
    val entries: StateFlow<List<PendingSale>>

    /** Store [entry], replacing any entry with the same id. */
    fun put(entry: PendingSale)

    fun remove(id: String)
}
