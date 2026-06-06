@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)

package app.devper.pharm.domain.observer

import app.devper.pharm.common.PrintlnLogger
import app.devper.pharm.common.platform.ConnectivityObserver
import app.devper.pharm.domain.model.PendingSale
import app.devper.pharm.domain.model.Sale
import app.devper.pharm.domain.param.CheckoutParam
import app.devper.pharm.domain.param.EnqueueOfflineSaleParam
import app.devper.pharm.domain.param.MarkOfflineSaleFailedParam
import app.devper.pharm.domain.param.VoidSaleParam
import app.devper.pharm.domain.repository.OfflineSaleQueue
import app.devper.pharm.domain.repository.SaleRepository
import app.devper.pharm.domain.testDispatchers
import app.devper.pharm.domain.usecase.RetryOfflineSaleUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class OfflineAutoSyncTest {

    private fun pending(id: String) = PendingSale(
        id = id, clientRequestId = "c-$id", payloadJson = "payload-$id", enqueuedAt = 0L,
    )

    private fun TestScope.autoSync(online: MutableStateFlow<Boolean>, queue: Queue, sales: Sales): OfflineAutoSync {
        val d = UnconfinedTestDispatcher(testScheduler)
        return OfflineAutoSync(
            connectivity = object : ConnectivityObserver { override val online = online },
            queue = queue,
            retry = RetryOfflineSaleUseCase(queue, sales, testDispatchers(d)),
            logger = PrintlnLogger(),
        )
    }

    @Test
    fun start_syncs_all_pending_when_online_becomes_true() = runTest {
        val online = MutableStateFlow(false)
        val queue = Queue(listOf(pending("p1"), pending("p2")))
        val sales = Sales()
        val scope = CoroutineScope(UnconfinedTestDispatcher(testScheduler))
        autoSync(online, queue, sales).start(scope)

        online.value = true
        advanceUntilIdle()

        assertEquals(2, sales.replayCount)
        assertEquals(setOf("p1", "p2"), queue.synced)
        scope.cancel()
    }

    @Test
    fun syncPending_with_empty_queue_is_a_noop() = runTest {
        val sales = Sales()
        autoSync(MutableStateFlow(true), Queue(emptyList()), sales).syncPending()
        assertEquals(0, sales.replayCount)
    }
}

private class Queue(initial: List<PendingSale>) : OfflineSaleQueue {
    private val _pending = MutableStateFlow(initial)
    override val pending: StateFlow<List<PendingSale>> = _pending.asStateFlow()
    val synced = mutableSetOf<String>()

    override fun enqueue(param: EnqueueOfflineSaleParam): String = ""
    override fun markSynced(id: String) { synced += id }
    override fun markFailed(param: MarkOfflineSaleFailedParam) {}
    override fun clear() {}
}

private class Sales : SaleRepository {
    var replayCount = 0
        private set

    override suspend fun checkout(param: CheckoutParam): Sale = sale()
    override suspend fun void(param: VoidSaleParam) {}
    override fun serializeCheckout(param: CheckoutParam): String = ""
    override suspend fun replayCheckout(payloadJson: String): Sale {
        replayCount++
        return sale()
    }

    private fun sale() = Sale("s", "B", 0.0, 0.0, 0.0, emptyList())
}
