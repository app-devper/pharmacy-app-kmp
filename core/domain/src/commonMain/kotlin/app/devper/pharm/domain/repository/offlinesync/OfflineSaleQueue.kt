package app.devper.pharm.domain.repository

import app.devper.pharm.domain.model.PendingSale
import app.devper.pharm.domain.param.EnqueueOfflineSaleParam
import app.devper.pharm.domain.param.MarkOfflineSaleFailedParam
import kotlinx.coroutines.flow.StateFlow

interface OfflineSaleQueue {
    val pending: StateFlow<List<PendingSale>>

    fun enqueue(param: EnqueueOfflineSaleParam): String

    fun markSynced(id: String)

    fun markFailed(param: MarkOfflineSaleFailedParam)

    fun clear()
}
