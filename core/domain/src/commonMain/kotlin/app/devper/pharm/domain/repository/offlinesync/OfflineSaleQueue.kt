package app.devper.pharm.domain.repository.offlinesync

import app.devper.pharm.domain.model.PendingSale
import app.devper.pharm.domain.param.offlinesync.EnqueueOfflineSaleParam
import app.devper.pharm.domain.param.offlinesync.MarkOfflineSaleFailedParam
import kotlinx.coroutines.flow.StateFlow

interface OfflineSaleQueue {
    val pending: StateFlow<List<PendingSale>>

    fun enqueue(param: EnqueueOfflineSaleParam): String

    fun markSynced(id: String)

    fun markFailed(param: MarkOfflineSaleFailedParam)

    fun clear()
}
