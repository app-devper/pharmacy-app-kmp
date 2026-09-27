package app.devper.pharm.domain.repository.offlinesync

import app.devper.pharm.domain.model.KyForm
import app.devper.pharm.domain.model.PendingSale
import app.devper.pharm.domain.param.offlinesync.EnqueueOfflineSaleParam
import app.devper.pharm.domain.param.offlinesync.MarkOfflineSaleFailedParam
import kotlinx.coroutines.flow.StateFlow

interface OfflineSaleQueue {
    val pending: StateFlow<List<PendingSale>>

    fun enqueue(param: EnqueueOfflineSaleParam): String

    fun markSynced(id: String)

    fun markFailed(param: MarkOfflineSaleFailedParam)

    /** Replace the KY forms still waiting on a queued bill. */
    fun setKyForms(id: String, forms: List<KyForm>)

    fun clear()
}
