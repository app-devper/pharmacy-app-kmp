package app.devper.pharm.presentation.offlinesync

import app.devper.pharm.common.AppDispatchers
import app.devper.pharm.domain.model.PendingSale
import app.devper.pharm.domain.observer.OfflineQueueProvider
import app.devper.pharm.domain.repository.FakeOfflineSaleQueue
import app.devper.pharm.domain.repository.FakeSaleRepository
import app.devper.pharm.domain.usecase.MarkOfflineSaleSyncedUseCase
import app.devper.pharm.domain.usecase.RetryOfflineSaleUseCase
import app.devper.pharm.presentation.offlinesync.exception.OfflineSyncUiStateError
import app.devper.pharm.presentation.offlinesync.message.OfflineSyncUiStateMessage
import app.devper.pharm.ui.common.runVmTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class OfflineSyncViewModelTest {

    private fun newVm(
        dispatchers: AppDispatchers,
        queue: FakeOfflineSaleQueue = FakeOfflineSaleQueue(),
        sales: FakeSaleRepository = FakeSaleRepository(),
    ): Triple<OfflineSyncViewModel, FakeOfflineSaleQueue, FakeSaleRepository> {
        val vm = OfflineSyncViewModel(
            offlineQueue = OfflineQueueProvider(queue),
            markSynced = MarkOfflineSaleSyncedUseCase(queue, dispatchers),
            retrySale = RetryOfflineSaleUseCase(queue, sales, dispatchers),
            timeZoneProvider = app.devper.pharm.domain.observer.testTimeZoneProvider(),
        )
        return Triple(vm, queue, sales)
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
        val (vm, _, _) = newVm(
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
        val (vm, queue, _) = newVm(dispatchers)
        advanceUntilIdle()
        assertEquals(0, vm.state.value.pending.size)
        queue.push(pending("x", enqueuedAt = 100))
        advanceUntilIdle()
        assertEquals(1, vm.state.value.pending.size)
        assertEquals("x", vm.state.value.pending[0].id)
    }

    @Test
    fun askDiscard_sets_confirm_id() = runVmTest { dispatchers ->
        val (vm, _, _) = newVm(dispatchers)
        vm.askDiscard("foo")
        assertEquals("foo", vm.state.value.confirmDiscardId)
    }

    @Test
    fun cancelDiscard_clears_confirm_id() = runVmTest { dispatchers ->
        val (vm, _, _) = newVm(dispatchers)
        vm.askDiscard("foo")
        vm.cancelDiscard()
        assertNull(vm.state.value.confirmDiscardId)
    }

    @Test
    fun discardConfirmed_calls_markSynced_and_clears_id_with_message() = runVmTest { dispatchers ->
        val (vm, queue, _) = newVm(
            dispatchers,
            FakeOfflineSaleQueue(seed = listOf(pending("foo", enqueuedAt = 100))),
        )
        advanceUntilIdle()
        vm.askDiscard("foo")
        vm.discardConfirmed()
        advanceUntilIdle()
        assertEquals("foo", queue.lastMarkSynced)
        assertNull(vm.state.value.confirmDiscardId)
        assertIs<OfflineSyncUiStateMessage.Discarded>(vm.state.value.messageState)

        assertTrue(vm.state.value.pending.none { it.id == "foo" })
    }

    @Test
    fun discardConfirmed_keeps_confirm_id_and_surfaces_error_on_markSynced_failure() = runVmTest { dispatchers ->
        val (vm, _, _) = newVm(
            dispatchers,
            FakeOfflineSaleQueue(
                seed = listOf(pending("foo", enqueuedAt = 100)),
                markSyncedThrows = RuntimeException("disk full"),
            ),
        )
        advanceUntilIdle()
        vm.askDiscard("foo")
        vm.discardConfirmed()
        advanceUntilIdle()
        assertEquals("foo", vm.state.value.confirmDiscardId)
        assertNull(vm.state.value.messageState)
        val error = vm.state.value.errorState
        assertIs<OfflineSyncUiStateError.DiscardFailed>(error)
        assertEquals("disk full", error.cause?.message)
        assertTrue(vm.state.value.pending.any { it.id == "foo" })
    }

    @Test
    fun discardConfirmed_no_op_when_no_confirm_id() = runVmTest { dispatchers ->
        val (vm, queue, _) = newVm(dispatchers)

        vm.discardConfirmed()
        advanceUntilIdle()
        assertNull(queue.lastMarkSynced)
        assertNull(vm.state.value.messageState)
    }

    @Test
    fun dismissMessage_clears_message() = runVmTest { dispatchers ->
        val (vm, _, _) = newVm(
            dispatchers,
            FakeOfflineSaleQueue(seed = listOf(pending("x", enqueuedAt = 100))),
        )
        advanceUntilIdle()
        vm.askDiscard("x")
        vm.discardConfirmed()
        advanceUntilIdle()
        assertNotNull(vm.state.value.messageState)
        vm.dismissMessage()
        assertNull(vm.state.value.messageState)
    }

    @Test
    fun failedCount_reflects_lastError_non_null() = runVmTest { dispatchers ->
        val (vm, _, _) = newVm(
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

    @Test
    fun retry_calls_replay_and_marks_synced_on_success() = runVmTest { dispatchers ->
        val seed = pending("p1", enqueuedAt = 100)
        val (vm, queue, sales) = newVm(
            dispatchers,
            FakeOfflineSaleQueue(seed = listOf(seed)),
        )
        advanceUntilIdle()
        vm.retry("p1")
        advanceUntilIdle()
        assertEquals(seed.payloadJson, sales.lastReplay)
        assertEquals("p1", queue.lastMarkSynced)
        assertTrue(vm.state.value.pending.none { it.id == "p1" })
        assertNull(vm.state.value.errorState)
    }

    @Test
    fun retry_marks_failed_and_surfaces_error_when_replay_throws() = runVmTest { dispatchers ->
        val seed = pending("p1", enqueuedAt = 100)
        val (vm, queue, _) = newVm(
            dispatchers,
            FakeOfflineSaleQueue(seed = listOf(seed)),
            FakeSaleRepository(replayThrows = RuntimeException("boom")),
        )
        advanceUntilIdle()
        vm.retry("p1")
        advanceUntilIdle()
        assertNull(queue.lastMarkSynced)
        val error = vm.state.value.errorState
        assertIs<OfflineSyncUiStateError.RetryFailed>(error)
        assertEquals("p1", error.billId)
        assertEquals("boom", error.cause?.message)
        assertTrue(vm.state.value.pending.any { it.id == "p1" })
    }

    @Test
    fun syncAll_retries_every_pending_in_snapshot() = runVmTest { dispatchers ->
        val (vm, queue, sales) = newVm(
            dispatchers,
            FakeOfflineSaleQueue(
                seed = listOf(
                    pending("a", enqueuedAt = 100),
                    pending("b", enqueuedAt = 200),
                ),
            ),
        )
        advanceUntilIdle()
        vm.syncAll()
        advanceUntilIdle()
        assertEquals(0, vm.state.value.pending.size)
        assertEquals("b", queue.lastMarkSynced)
        assertNotNull(sales.lastReplay)
    }

    @Test
    fun syncAll_no_op_when_pending_is_empty() = runVmTest { dispatchers ->
        val (vm, _, sales) = newVm(dispatchers)
        advanceUntilIdle()
        vm.syncAll()
        advanceUntilIdle()
        assertNull(sales.lastReplay)
        assertNull(vm.state.value.messageState)
    }

    @Test
    fun syncAll_sets_sync_started_message_with_count() = runVmTest { dispatchers ->
        val (vm, _, _) = newVm(
            dispatchers,
            FakeOfflineSaleQueue(
                seed = listOf(
                    pending("a", enqueuedAt = 100),
                    pending("b", enqueuedAt = 200),
                ),
            ),
        )
        advanceUntilIdle()
        vm.syncAll()
        val message = vm.state.value.messageState
        assertIs<OfflineSyncUiStateMessage.SyncStarted>(message)
        assertEquals(2, message.count)
        advanceUntilIdle()
    }

    @Test
    fun syncAll_partial_failure_sets_typed_error_with_counts() = runVmTest { dispatchers ->
        val (vm, _, _) = newVm(
            dispatchers,
            FakeOfflineSaleQueue(
                seed = listOf(
                    pending("a", enqueuedAt = 100),
                    pending("b", enqueuedAt = 200),
                ),
            ),
            FakeSaleRepository(replayThrows = RuntimeException("network")),
        )
        advanceUntilIdle()
        vm.syncAll()
        advanceUntilIdle()
        val error = vm.state.value.errorState
        assertIs<OfflineSyncUiStateError.SyncPartialFailed>(error)
        assertEquals(2, error.failed)
        assertEquals(2, error.total)
    }

    @Test
    fun retry_sets_retry_started_message_with_truncated_id() = runVmTest { dispatchers ->
        val (vm, _, _) = newVm(
            dispatchers,
            FakeOfflineSaleQueue(seed = listOf(pending("abcdefghij", enqueuedAt = 100))),
        )
        advanceUntilIdle()
        vm.retry("abcdefghij")
        val message = vm.state.value.messageState
        assertIs<OfflineSyncUiStateMessage.RetryStarted>(message)
        assertEquals("abcdefgh", message.billId)
        advanceUntilIdle()
    }

    @Test
    fun refresh_sets_refreshed_message() = runVmTest { dispatchers ->
        val (vm, _, _) = newVm(dispatchers)
        advanceUntilIdle()
        vm.refresh()
        assertIs<OfflineSyncUiStateMessage.Refreshed>(vm.state.value.messageState)
    }
}
