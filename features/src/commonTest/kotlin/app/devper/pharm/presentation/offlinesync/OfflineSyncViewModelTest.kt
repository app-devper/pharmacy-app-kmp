package app.devper.pharm.presentation.offlinesync

import app.devper.pharm.common.AppDispatchers
import app.devper.pharm.domain.model.PendingSale
import app.devper.pharm.domain.observer.OfflineQueueProvider
import app.devper.pharm.domain.repository.FakeOfflineSaleQueue
import app.devper.pharm.domain.usecase.MarkOfflineSaleSyncedUseCase
import app.devper.pharm.ui.common.runVmTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class OfflineSyncViewModelTest {

    private fun newVm(
        @Suppress("UNUSED_PARAMETER") dispatchers: AppDispatchers,
        queue: FakeOfflineSaleQueue = FakeOfflineSaleQueue(),
    ): Pair<OfflineSyncViewModel, FakeOfflineSaleQueue> {

        val vm = OfflineSyncViewModel(
            offlineQueue = OfflineQueueProvider(queue),
            markSynced = MarkOfflineSaleSyncedUseCase(queue),
        )
        return vm to queue
    }

    private fun pending(id: String, enqueuedAt: Long, lastError: String? = null) = PendingSale(
        id = id,
        clientRequestId = "crid-$id",
        payloadJson = """{"crid":"$id"}""",
        enqueuedAt = enqueuedAt,
        lastError = lastError,
    )

    @Test
    fun init_sorts_pending_by_enqueuedAt_ascending() = runVmTest { dispatchers ->
        val (vm, _) = newVm(
            dispatchers,
            FakeOfflineSaleQueue(
                seed = listOf(
                    pending("c", enqueuedAt = 3000),
                    pending("a", enqueuedAt = 1000),
                    pending("b", enqueuedAt = 2000),
                ),
            ),
        )
        advanceUntilIdle()
        val ids = vm.state.value.pending.map { it.id }
        assertEquals(listOf("a", "b", "c"), ids)
    }

    @Test
    fun init_subscribes_to_queue_mutations() = runVmTest { dispatchers ->
        val (vm, queue) = newVm(dispatchers)
        advanceUntilIdle()
        assertEquals(0, vm.state.value.pending.size)
        queue.push(pending("x", enqueuedAt = 100))
        advanceUntilIdle()
        assertEquals(1, vm.state.value.pending.size)
        assertEquals("x", vm.state.value.pending[0].id)
    }

    @Test
    fun askDiscard_sets_confirm_id() = runVmTest { dispatchers ->
        val (vm, _) = newVm(dispatchers)
        vm.askDiscard("foo")
        assertEquals("foo", vm.state.value.confirmDiscardId)
    }

    @Test
    fun cancelDiscard_clears_confirm_id() = runVmTest { dispatchers ->
        val (vm, _) = newVm(dispatchers)
        vm.askDiscard("foo")
        vm.cancelDiscard()
        assertNull(vm.state.value.confirmDiscardId)
    }

    @Test
    fun discardConfirmed_calls_markSynced_and_clears_id_with_message() = runVmTest { dispatchers ->
        val (vm, queue) = newVm(
            dispatchers,
            FakeOfflineSaleQueue(seed = listOf(pending("foo", enqueuedAt = 100))),
        )
        advanceUntilIdle()
        vm.askDiscard("foo")
        vm.discardConfirmed()
        advanceUntilIdle()
        assertEquals("foo", queue.lastMarkSynced)
        assertNull(vm.state.value.confirmDiscardId)
        assertEquals("ลบรายการค้างซิงก์แล้ว", vm.state.value.message)

        assertTrue(vm.state.value.pending.none { it.id == "foo" })
    }

    @Test
    fun discardConfirmed_no_op_when_no_confirm_id() = runVmTest { dispatchers ->
        val (vm, queue) = newVm(dispatchers)

        vm.discardConfirmed()
        advanceUntilIdle()
        assertNull(queue.lastMarkSynced)
        assertNull(vm.state.value.message)
    }

    @Test
    fun dismissMessage_clears_message() = runVmTest { dispatchers ->
        val (vm, _) = newVm(
            dispatchers,
            FakeOfflineSaleQueue(seed = listOf(pending("x", enqueuedAt = 100))),
        )
        advanceUntilIdle()
        vm.askDiscard("x")
        vm.discardConfirmed()
        advanceUntilIdle()
        assertNotNull(vm.state.value.message)
        vm.dismissMessage()
        assertNull(vm.state.value.message)
    }

    @Test
    fun failedCount_reflects_lastError_non_null() = runVmTest { dispatchers ->
        val (vm, _) = newVm(
            dispatchers,
            FakeOfflineSaleQueue(
                seed = listOf(
                    pending("ok", enqueuedAt = 100, lastError = null),
                    pending("err1", enqueuedAt = 200, lastError = "timeout"),
                    pending("err2", enqueuedAt = 300, lastError = "401"),
                ),
            ),
        )
        advanceUntilIdle()
        assertEquals(3, vm.state.value.totalCount)
        assertEquals(2, vm.state.value.failedCount)
    }
}
