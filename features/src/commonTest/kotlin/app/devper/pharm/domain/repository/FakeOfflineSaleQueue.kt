package app.devper.pharm.domain.repository

import app.devper.pharm.domain.model.PendingSale
import app.devper.pharm.domain.param.EnqueueOfflineSaleParam
import app.devper.pharm.domain.param.MarkOfflineSaleFailedParam
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class FakeOfflineSaleQueue(
    seed: List<PendingSale> = emptyList(),
) : OfflineSaleQueue {

    private val pendingState = MutableStateFlow(seed)
    override val pending: StateFlow<List<PendingSale>> = pendingState.asStateFlow()

    var lastEnqueue: EnqueueOfflineSaleParam? = null
        private set
    var lastMarkSynced: String? = null
        private set
    var lastMarkFailed: MarkOfflineSaleFailedParam? = null
        private set

    fun push(sale: PendingSale) {
        pendingState.value = pendingState.value + sale
    }

    override fun enqueue(param: EnqueueOfflineSaleParam): String {
        lastEnqueue = param
        val id = "fake-${pendingState.value.size}"
        val now = pendingState.value.size.toLong() * 1000L
        pendingState.value = pendingState.value + PendingSale(
            id = id,
            clientRequestId = param.clientRequestId,
            payloadJson = param.payloadJson,
            enqueuedAt = now,
        )
        return id
    }

    override fun markSynced(id: String) {
        lastMarkSynced = id
        pendingState.value = pendingState.value.filterNot { it.id == id }
    }

    override fun markFailed(param: MarkOfflineSaleFailedParam) {
        lastMarkFailed = param
    }

    override fun clear() {
        pendingState.value = emptyList()
    }
}
