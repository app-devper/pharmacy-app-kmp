package app.devper.pharm.presentation.offlinesync

import app.devper.pharm.common.ConflictException
import app.devper.pharm.common.ForbiddenException
import app.devper.pharm.domain.model.PendingSale
import app.devper.pharm.domain.model.PendingSaleState
import app.devper.pharm.domain.repository.FakeOfflineSaleQueue
import app.devper.pharm.domain.repository.FakeSaleRepository
import app.devper.pharm.domain.repository.pendingSalesOf
import app.devper.pharm.presentation.offlinesync.exception.OfflineSyncUiStateError
import app.devper.pharm.presentation.offlinesync.message.OfflineSyncUiStateMessage
import app.devper.pharm.ui.common.runVmTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class OfflineSyncViewModelTest {

    private fun newVm(
        queue: FakeOfflineSaleQueue = FakeOfflineSaleQueue(),
        sales: FakeSaleRepository = FakeSaleRepository(),
    ) = OfflineSyncViewModel(
        pendingSales = pendingSalesOf(queue, sales),
        timeZoneProvider = app.devper.pharm.domain.observer.testTimeZoneProvider(),
    )

    private fun pending(id: String, enqueuedAt: Long = 100, state: PendingSaleState = PendingSaleState.Pending) = PendingSale(
        id = id,
        clientRequestId = "crid-$id",
        payloadJson = """{"crid":"$id"}""",
        enqueuedAt = enqueuedAt,
        state = state,
    )

    @Test
    fun entries_are_listed_oldest_first_and_follow_the_queue() = runVmTest {
        val queue = FakeOfflineSaleQueue(listOf(pending("c", 3000), pending("a", 1000), pending("b", 2000)))
        val vm = newVm(queue)
        advanceUntilIdle()
        assertEquals(listOf("a", "b", "c"), vm.state.value.pending.map { it.id })

        queue.remove("a")
        advanceUntilIdle()
        assertEquals(listOf("b", "c"), vm.state.value.pending.map { it.id })
    }

    @Test
    fun counts_separate_waiting_from_needing_a_person() = runVmTest {
        val vm = newVm(
            FakeOfflineSaleQueue(
                listOf(pending("a"), pending("b", state = PendingSaleState.Conflict), pending("c", state = PendingSaleState.Damaged)),
            ),
        )
        advanceUntilIdle()
        assertEquals(3, vm.state.value.totalCount)
        assertEquals(1, vm.state.value.retryableCount)
        assertEquals(2, vm.state.value.needsActionCount)
    }

    @Test
    fun syncAll_sends_only_waiting_entries() = runVmTest {
        val queue = FakeOfflineSaleQueue(listOf(pending("a"), pending("b", state = PendingSaleState.Conflict)))
        val sales = FakeSaleRepository()
        val vm = newVm(queue, sales)
        advanceUntilIdle()

        vm.syncAll()
        advanceUntilIdle()

        assertEquals(1, sales.replayCount)
        assertEquals(listOf("b"), vm.state.value.pending.map { it.id })
        assertNull(vm.state.value.errorState)
    }

    @Test
    fun syncAll_reports_refusals() = runVmTest {
        val queue = FakeOfflineSaleQueue(listOf(pending("a", 1), pending("b", 2)))
        val vm = newVm(queue, FakeSaleRepository(replayThrows = ConflictException(payload = """{"error":"no"}""")))
        advanceUntilIdle()

        vm.syncAll()
        val started = vm.state.value.messageState
        assertIs<OfflineSyncUiStateMessage.SyncStarted>(started)
        assertEquals(2, started.count)
        advanceUntilIdle()

        val error = vm.state.value.errorState
        assertIs<OfflineSyncUiStateError.SyncPartialFailed>(error)
        assertEquals(2, error.failed)
        assertTrue(vm.state.value.pending.all { it.state == PendingSaleState.Conflict })
    }

    @Test
    fun retry_that_records_the_sale_says_so() = runVmTest {
        val queue = FakeOfflineSaleQueue(listOf(pending("p1", state = PendingSaleState.Conflict)))
        val vm = newVm(queue)
        advanceUntilIdle()

        vm.retry("p1")
        advanceUntilIdle()

        assertTrue(vm.state.value.pending.isEmpty())
        assertEquals(OfflineSyncUiStateMessage.Recorded, vm.state.value.messageState)
    }

    @Test
    fun retry_still_refused_keeps_the_entry_and_says_so() = runVmTest {
        val queue = FakeOfflineSaleQueue(listOf(pending("abcdefgh-1", state = PendingSaleState.Conflict)))
        val vm = newVm(queue, FakeSaleRepository(replayThrows = ConflictException(payload = """{"error":"no"}""")))
        advanceUntilIdle()

        vm.retry("abcdefgh-1")
        advanceUntilIdle()

        val error = vm.state.value.errorState
        assertIs<OfflineSyncUiStateError.StillNotRecorded>(error)
        assertEquals("abcdefgh", error.billId)
        assertEquals(PendingSaleState.Conflict, vm.state.value.pending.single().state)
    }

    @Test
    fun abandon_needs_a_reason_then_records_it() = runVmTest {
        val queue = FakeOfflineSaleQueue(listOf(pending("p1", state = PendingSaleState.Conflict)))
        val sales = FakeSaleRepository()
        val vm = newVm(queue, sales)
        advanceUntilIdle()

        vm.askAbandon("p1")
        assertEquals(Resolving.Abandon("p1", kyOnly = false), vm.state.value.resolving)
        vm.confirmResolving()
        advanceUntilIdle()
        assertTrue(sales.abandoned.isEmpty())

        vm.reasonChanged("customer left")
        vm.confirmResolving()
        advanceUntilIdle()

        assertEquals("customer left", sales.abandoned.single().third)
        assertNull(vm.state.value.resolving)
        assertTrue(vm.state.value.pending.isEmpty())
        assertEquals(OfflineSyncUiStateMessage.Abandoned, vm.state.value.messageState)
    }

    @Test
    fun a_refused_abandonment_keeps_the_dialog_and_the_entry() = runVmTest {
        val queue = FakeOfflineSaleQueue(listOf(pending("p1", state = PendingSaleState.Conflict)))
        val vm = newVm(queue, FakeSaleRepository(abandonThrows = ForbiddenException()))
        advanceUntilIdle()

        vm.askAbandon("p1")
        vm.reasonChanged("r")
        vm.confirmResolving()
        advanceUntilIdle()

        assertIs<OfflineSyncUiStateError.AbandonFailed>(vm.state.value.errorState)
        assertEquals("p1", vm.state.value.resolving?.id)
        assertEquals(1, vm.state.value.pending.size)
    }

    @Test
    fun ky_pending_entries_close_only_their_ky_forms() = runVmTest {
        val vm = newVm(FakeOfflineSaleQueue(listOf(pending("p1", state = PendingSaleState.KyPending))))
        advanceUntilIdle()
        vm.askAbandon("p1")
        assertEquals(Resolving.Abandon("p1", kyOnly = true), vm.state.value.resolving)
    }

    @Test
    fun a_damaged_entry_can_be_discarded_only_after_export() = runVmTest {
        val queue = FakeOfflineSaleQueue(listOf(pending("bad", state = PendingSaleState.Damaged)))
        val vm = newVm(queue)
        advanceUntilIdle()

        vm.askAbandon("bad")
        vm.askDiscard("bad")
        assertNull(vm.state.value.resolving)

        vm.export("bad")
        advanceUntilIdle()
        assertIs<OfflineSyncUiStateMessage.Exported>(vm.state.value.messageState)

        vm.askDiscard("bad")
        assertEquals(Resolving.DiscardDamaged("bad"), vm.state.value.resolving)
        vm.confirmResolving()
        advanceUntilIdle()
        assertTrue(vm.state.value.pending.isEmpty())
        assertEquals(OfflineSyncUiStateMessage.Discarded, vm.state.value.messageState)
    }

    @Test
    fun dismissMessage_clears_message() = runVmTest {
        val vm = newVm(FakeOfflineSaleQueue(listOf(pending("a"))))
        advanceUntilIdle()
        vm.syncAll()
        vm.dismissMessage()
        advanceUntilIdle()
        assertNull(vm.state.value.messageState)
    }
}
