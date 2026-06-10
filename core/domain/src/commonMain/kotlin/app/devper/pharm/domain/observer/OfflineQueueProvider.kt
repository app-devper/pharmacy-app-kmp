package app.devper.pharm.domain.observer

import app.devper.pharm.domain.model.PendingSale
import app.devper.pharm.domain.repository.offlinesync.OfflineSaleQueue
import kotlinx.coroutines.flow.StateFlow

class OfflineQueueProvider(private val queue: OfflineSaleQueue) {
    val pending: StateFlow<List<PendingSale>> get() = queue.pending
}
