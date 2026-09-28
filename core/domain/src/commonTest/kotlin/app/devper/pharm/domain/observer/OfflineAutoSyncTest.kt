@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)

package app.devper.pharm.domain.observer

import app.devper.pharm.common.PrintlnLogger
import app.devper.pharm.common.platform.ConnectivityObserver
import app.devper.pharm.domain.model.PendingSale
import app.devper.pharm.domain.repository.FakeOfflineSaleQueue
import app.devper.pharm.domain.repository.FakeSaleRepository
import app.devper.pharm.domain.repository.pendingSalesOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class OfflineAutoSyncTest {

    private fun pending(id: String) = PendingSale(id = id, clientRequestId = "c-$id", payloadJson = "payload-$id", enqueuedAt = 0L)

    private fun autoSync(online: MutableStateFlow<Boolean>, queue: FakeOfflineSaleQueue, sales: FakeSaleRepository) =
        OfflineAutoSync(
            connectivity = object : ConnectivityObserver { override val online = online },
            pendingSales = pendingSalesOf(queue, sales),
            logger = PrintlnLogger(),
        )

    @Test
    fun start_syncs_all_pending_when_online_becomes_true() = runTest {
        val online = MutableStateFlow(false)
        val queue = FakeOfflineSaleQueue(listOf(pending("p1"), pending("p2")))
        val sales = FakeSaleRepository()
        val scope = CoroutineScope(UnconfinedTestDispatcher(testScheduler))
        autoSync(online, queue, sales).start(scope)

        online.value = true
        advanceUntilIdle()

        assertEquals(2, sales.replayCount)
        assertTrue(queue.entries.value.isEmpty())
        scope.cancel()
    }

    @Test
    fun syncPending_with_empty_queue_is_a_noop() = runTest {
        val sales = FakeSaleRepository()
        autoSync(MutableStateFlow(true), FakeOfflineSaleQueue(), sales).syncPending()
        assertEquals(0, sales.replayCount)
    }
}
