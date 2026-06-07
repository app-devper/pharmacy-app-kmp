@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)

package app.devper.pharm.domain.observer

import app.devper.pharm.common.value.Money

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

    @Test
    fun syncPending_aborts_loop_on_network_error_to_avoid_attempts_inflation() = runTest {
        val queue = Queue(listOf(pending("p1"), pending("p2"), pending("p3")))
        val sales = Sales(failWith = RuntimeException("Network is unreachable"))
        autoSync(MutableStateFlow(true), queue, sales).syncPending()

        assertEquals(1, sales.replayCount)
        assertEquals(setOf("p1"), queue.failedIds)
    }

    @Test
    fun syncPending_continues_loop_on_non_network_error() = runTest {
        val queue = Queue(listOf(pending("p1"), pending("p2"), pending("p3")))
        val sales = Sales(failWith = IllegalStateException("validation error"))
        autoSync(MutableStateFlow(true), queue, sales).syncPending()

        assertEquals(3, sales.replayCount)
        assertEquals(setOf("p1", "p2", "p3"), queue.failedIds)
    }
}

private class Queue(initial: List<PendingSale>) : OfflineSaleQueue {
    private val _pending = MutableStateFlow(initial)
    override val pending: StateFlow<List<PendingSale>> = _pending.asStateFlow()
    val synced = mutableSetOf<String>()
    val failedIds = mutableSetOf<String>()

    override fun enqueue(param: EnqueueOfflineSaleParam): String = ""
    override fun markSynced(id: String) { synced += id }
    override fun markFailed(param: MarkOfflineSaleFailedParam) { failedIds += param.id }
    override fun clear() {}
}

private class Sales(private val failWith: Throwable? = null) : SaleRepository {
    var replayCount = 0
        private set

    override suspend fun checkout(param: CheckoutParam): Sale = sale()
    override suspend fun void(param: VoidSaleParam) {}
    override fun serializeCheckout(param: CheckoutParam): String = ""
    override suspend fun replayCheckout(payloadJson: String): Sale {
        replayCount++
        failWith?.let { throw it }
        return sale()
    }

    private fun sale() = Sale("s", "B", Money.Zero, Money.Zero, Money.Zero, emptyList())
}
