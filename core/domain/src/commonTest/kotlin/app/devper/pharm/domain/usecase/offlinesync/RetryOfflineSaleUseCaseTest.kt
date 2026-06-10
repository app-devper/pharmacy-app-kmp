@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)

package app.devper.pharm.domain.usecase

import app.devper.pharm.domain.usecase.offlinesync.RetryOfflineSaleUseCase

import app.devper.pharm.common.value.Money

import app.devper.pharm.domain.testDispatchers
import app.devper.pharm.common.NotFoundException
import app.devper.pharm.domain.model.PendingSale
import app.devper.pharm.domain.model.Sale
import app.devper.pharm.domain.param.CheckoutParam
import app.devper.pharm.domain.param.EnqueueOfflineSaleParam
import app.devper.pharm.domain.param.MarkOfflineSaleFailedParam
import app.devper.pharm.domain.param.VoidSaleParam
import app.devper.pharm.domain.repository.OfflineSaleQueue
import app.devper.pharm.domain.repository.SaleRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class RetryOfflineSaleUseCaseTest {

    private fun pending(id: String) = PendingSale(
        id = id, clientRequestId = "crid-$id", payloadJson = "payload-$id", enqueuedAt = 0L,
    )

    @Test
    fun unknown_id_fails_with_not_found() = runTest {
        val queue = FakeOfflineQueue()
        val result = RetryOfflineSaleUseCase(queue, FakeReplaySales(), testDispatchers()).invoke("missing")
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is NotFoundException)
        assertNull(queue.syncedId)
        assertNull(queue.failedParam)
    }

    @Test
    fun successful_replay_marks_synced_and_returns_sale() = runTest {
        val queue = FakeOfflineQueue(listOf(pending("p1")))
        val sales = FakeReplaySales(sale = Sale("s1", "B1", Money(10.0), Money(0.0), Money(0.0), emptyList()))
        val result = RetryOfflineSaleUseCase(queue, sales, testDispatchers()).invoke("p1")
        assertEquals("s1", result.getOrThrow().id)
        assertEquals("payload-p1", sales.replayedPayload)
        assertEquals("p1", queue.syncedId)
        assertNull(queue.failedParam)
    }

    @Test
    fun replay_failure_marks_failed_and_rethrows() = runTest {
        val queue = FakeOfflineQueue(listOf(pending("p1")))
        val boom = RuntimeException("server 500")
        val result = RetryOfflineSaleUseCase(queue, FakeReplaySales(failWith = boom), testDispatchers()).invoke("p1")
        assertTrue(result.isFailure)
        assertEquals(boom, result.exceptionOrNull())
        assertNull(queue.syncedId)
        assertEquals("p1", queue.failedParam?.id)
        assertEquals("server 500", queue.failedParam?.error)
    }
}

private class FakeOfflineQueue(initial: List<PendingSale> = emptyList()) : OfflineSaleQueue {
    private val _pending = MutableStateFlow(initial)
    override val pending: StateFlow<List<PendingSale>> = _pending.asStateFlow()

    var syncedId: String? = null
        private set
    var failedParam: MarkOfflineSaleFailedParam? = null
        private set

    override fun enqueue(param: EnqueueOfflineSaleParam): String = ""
    override fun markSynced(id: String) { syncedId = id }
    override fun markFailed(param: MarkOfflineSaleFailedParam) { failedParam = param }
    override fun clear() {}
}

private class FakeReplaySales(
    val sale: Sale = Sale("s1", "B1", Money(0.0), Money(0.0), Money(0.0), emptyList()),
    private val failWith: Throwable? = null,
) : SaleRepository {
    var replayedPayload: String? = null
        private set

    override suspend fun checkout(param: CheckoutParam): Sale = sale
    override suspend fun void(param: VoidSaleParam) {}
    override fun serializeCheckout(param: CheckoutParam): String = ""
    override suspend fun replayCheckout(payloadJson: String): Sale {
        replayedPayload = payloadJson
        failWith?.let { throw it }
        return sale
    }
}
