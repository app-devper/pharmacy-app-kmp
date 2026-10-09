package app.devper.pharm.domain.pendingsales

import app.devper.pharm.common.AuthException
import app.devper.pharm.common.ConflictException
import app.devper.pharm.common.ForbiddenException
import app.devper.pharm.common.IdentityUnavailableException
import app.devper.pharm.common.NetworkException
import app.devper.pharm.common.NotFoundException
import app.devper.pharm.common.ServerException
import app.devper.pharm.common.value.Money
import app.devper.pharm.domain.model.AbandonOutcome
import app.devper.pharm.domain.model.KyForm
import app.devper.pharm.domain.model.PendingSale
import app.devper.pharm.domain.model.PendingSaleState
import app.devper.pharm.domain.model.Sale
import app.devper.pharm.domain.repository.FakeFileDownloader
import app.devper.pharm.domain.repository.FakeKyRepository
import app.devper.pharm.domain.repository.FakeOfflineSaleQueue
import app.devper.pharm.domain.repository.FakeSaleRepository
import app.devper.pharm.domain.repository.pendingSalesOf
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class PendingSalesTest {

    private val recorded = Sale("s1", "B1", Money(10.0), Money.Zero, Money.Zero, emptyList())

    private fun entry(id: String, state: PendingSaleState = PendingSaleState.Pending, ky: List<KyForm> = emptyList()) =
        PendingSale(id = id, clientRequestId = "crid-$id", payloadJson = "payload-$id", enqueuedAt = id.hashCode().toLong(), kyForms = ky, state = state)

    private fun ky10() = KyForm.Ky10(
        saleId = "", date = LocalDate(2026, 9, 27), drugName = "Tramadol", regNo = "R1", qty = 1,
        unit = "tab", buyerName = "A", buyerAddress = "B", rxNo = "", doctor = "", balance = 0,
    )

    private fun ky11() = KyForm.Ky11(
        saleId = "", date = LocalDate(2026, 9, 27), drugName = "Dextro", regNo = "R2", qty = 1,
        unit = "tab", buyerName = "A", purpose = "cough", pharmacist = "P",
    )

    @Test
    fun enqueued_sale_is_pending_until_the_server_records_it() = runTest {
        val queue = FakeOfflineSaleQueue()
        val pending = pendingSalesOf(queue, FakeSaleRepository(successResult = recorded))
        val id = pending.enqueue("crid", "payload")
        assertEquals(PendingSaleState.Pending, queue.entry(id)?.state)

        val summary = pending.syncAll()

        assertEquals(SyncSummary(recorded = 1), summary)
        assertTrue(queue.entries.value.isEmpty())
    }

    @Test
    fun temporary_failures_keep_the_sale_pending() = runTest {
        val temporary = listOf(
            NetworkException(), IdentityUnavailableException(), AuthException(), ForbiddenException(),
            ServerException("Server error (502)", statusCode = 502), RuntimeException("Failed to connect to host"),
        )
        for (failure in temporary) {
            val queue = FakeOfflineSaleQueue(listOf(entry("p1")))
            pendingSalesOf(queue, FakeSaleRepository(replayThrows = failure)).syncAll()
            val e = queue.entry("p1")
            assertEquals(PendingSaleState.Pending, e?.state, "$failure")
            assertEquals(1, e?.attempts)
        }
    }

    @Test
    fun a_refusal_is_a_conflict_with_the_servers_reason() = runTest {
        val refusals = listOf(
            ConflictException(payload = """{"error":"this queued sale was abandoned"}"""),
            ServerException("HTTP error (400)", statusCode = 400, body = """{"error":"received must be >= total"}"""),
            ServerException("HTTP error (422)", statusCode = 422, body = "not json"),
        )
        val reasons = listOf("this queued sale was abandoned", "received must be >= total", "HTTP error (422)")
        refusals.zip(reasons).forEach { (failure, reason) ->
            val queue = FakeOfflineSaleQueue(listOf(entry("p1")))
            pendingSalesOf(queue, FakeSaleRepository(replayThrows = failure)).syncAll()
            assertEquals(PendingSaleState.Conflict, queue.entry("p1")?.state)
            assertEquals(reason, queue.entry("p1")?.lastError)
        }
    }

    @Test
    fun sync_leaves_entries_that_need_a_person_alone() = runTest {
        val queue = FakeOfflineSaleQueue(
            listOf(
                entry("a", PendingSaleState.Conflict),
                entry("b", PendingSaleState.KyPending, listOf(ky10())),
                entry("c", PendingSaleState.Damaged),
                entry("d"),
            ),
        )
        val sales = FakeSaleRepository()
        val summary = pendingSalesOf(queue, sales).syncAll()

        assertEquals(1, sales.replayCount)
        assertEquals(SyncSummary(recorded = 1), summary)
        assertEquals(listOf("a", "b", "c"), queue.entries.value.map { it.id }.sorted())
    }

    @Test
    fun sync_stops_at_the_first_outage() = runTest {
        val queue = FakeOfflineSaleQueue(listOf(entry("c"), entry("d"), entry("e")))
        val sales = FakeSaleRepository(replayThrows = NetworkException())
        val summary = pendingSalesOf(queue, sales).syncAll()

        assertEquals(1, sales.replayCount)
        assertEquals(SyncSummary(stillPending = 1), summary)
        assertEquals(3, queue.entries.value.size)
    }

    @Test
    fun queued_ky_forms_are_sent_with_the_recorded_sale_id() = runTest {
        val queue = FakeOfflineSaleQueue(listOf(entry("p1", ky = listOf(ky10(), ky11()))))
        val ky = FakeKyRepository()
        pendingSalesOf(queue, FakeSaleRepository(successResult = recorded), ky).syncAll()

        assertEquals(listOf("s1"), ky.ky10Submissions.map { it.saleId })
        assertEquals(listOf("s1"), ky.ky11Submissions.map { it.saleId })
        assertTrue(queue.entries.value.isEmpty())
    }

    @Test
    fun refused_ky_forms_leave_the_recorded_bill_ky_pending() = runTest {
        val queue = FakeOfflineSaleQueue(listOf(entry("p1", ky = listOf(ky10(), ky11()))))
        val ky = FakeKyRepository(ky10Throws = true)
        val summary = pendingSalesOf(queue, FakeSaleRepository(successResult = recorded), ky).syncAll()

        val e = queue.entry("p1")!!
        assertEquals(PendingSaleState.KyPending, e.state)
        assertEquals("B1", e.billNo)
        assertEquals(listOf("Tramadol"), e.kyForms.map { it.drugName })
        assertEquals("s1", (e.kyForms.single() as KyForm.Ky10).saleId)
        assertEquals(1, ky.ky11Submissions.size)
        assertEquals(SyncSummary(refused = 1), summary)
    }

    @Test
    fun an_outage_while_sending_ky_keeps_the_bill_pending() = runTest {
        val queue = FakeOfflineSaleQueue(listOf(entry("p1", ky = listOf(ky10(), ky11()))))
        val ky = FakeKyRepository(ky10Error = RuntimeException("Failed to connect to host"))
        pendingSalesOf(queue, FakeSaleRepository(successResult = recorded), ky).syncAll()

        val e = queue.entry("p1")!!
        assertEquals(PendingSaleState.Pending, e.state)
        assertEquals(2, e.kyForms.size)
        assertTrue(ky.ky11Submissions.isEmpty())
    }

    @Test
    fun a_5xx_while_sending_ky_keeps_the_bill_pending_instead_of_needing_a_person() = runTest {
        val queue = FakeOfflineSaleQueue(listOf(entry("p1", ky = listOf(ky10(), ky11()))))
        val ky = FakeKyRepository(ky10Error = ServerException("Server error (500)", statusCode = 500))
        pendingSalesOf(queue, FakeSaleRepository(successResult = recorded), ky).syncAll()

        assertEquals(PendingSaleState.Pending, queue.entry("p1")!!.state)
    }

    @Test
    fun retrying_a_conflict_records_it_or_keeps_it_in_conflict() = runTest {
        val queue = FakeOfflineSaleQueue(listOf(entry("p1", PendingSaleState.Conflict)))
        val sales = FakeSaleRepository(replayThrows = ConflictException(payload = """{"error":"still refused"}"""))
        val pending = pendingSalesOf(queue, sales)

        assertEquals(PendingSaleState.Conflict, pending.retry("p1").getOrThrow())
        assertEquals("still refused", queue.entry("p1")?.lastError)

        sales.replayThrows = null
        assertNull(pending.retry("p1").getOrThrow())
        assertTrue(queue.entries.value.isEmpty())
    }

    @Test
    fun retrying_an_unknown_or_damaged_entry_fails_without_sending() = runTest {
        val sales = FakeSaleRepository()
        val pending = pendingSalesOf(FakeOfflineSaleQueue(listOf(entry("x", PendingSaleState.Damaged))), sales)
        assertTrue(pending.retry("missing").exceptionOrNull() is NotFoundException)
        assertTrue(pending.retry("x").isFailure)
        assertEquals(0, sales.replayCount)
    }

    @Test
    fun abandoning_a_conflict_records_the_reason_then_removes_it() = runTest {
        val queue = FakeOfflineSaleQueue(listOf(entry("p1", PendingSaleState.Conflict)))
        val sales = FakeSaleRepository()
        pendingSalesOf(queue, sales).abandon("p1", " customer left ").getOrThrow()

        assertEquals(listOf(Triple("crid-p1", "sale", "customer left")), sales.abandoned)
        assertTrue(queue.entries.value.isEmpty())
    }

    @Test
    fun abandoning_needs_a_reason() = runTest {
        val queue = FakeOfflineSaleQueue(listOf(entry("p1", PendingSaleState.Conflict)))
        val sales = FakeSaleRepository()
        assertTrue(pendingSalesOf(queue, sales).abandon("p1", "  ").isFailure)
        assertTrue(sales.abandoned.isEmpty())
        assertEquals(1, queue.entries.value.size)
    }

    @Test
    fun a_failed_abandonment_keeps_the_entry() = runTest {
        val queue = FakeOfflineSaleQueue(listOf(entry("p1", PendingSaleState.Conflict)))
        val result = pendingSalesOf(queue, FakeSaleRepository(abandonThrows = ForbiddenException())).abandon("p1", "r")
        assertTrue(result.isFailure)
        assertEquals(1, queue.entries.value.size)
    }

    @Test
    fun a_sale_recorded_before_it_was_abandoned_is_done_unless_its_ky_forms_are_waiting() = runTest {
        val queue = FakeOfflineSaleQueue(listOf(entry("p1"), entry("p2", ky = listOf(ky10()))))
        val pending = pendingSalesOf(queue, FakeSaleRepository(abandonOutcome = AbandonOutcome.AlreadyRecorded(recorded)))

        pending.abandon("p1", "r").getOrThrow()
        pending.abandon("p2", "r").getOrThrow()

        assertNull(queue.entry("p1"))
        val kept = queue.entry("p2")!!
        assertEquals(PendingSaleState.Pending, kept.state)
        assertEquals("B1", kept.billNo)
        assertEquals("s1", (kept.kyForms.single() as KyForm.Ky10).saleId)
    }

    @Test
    fun closing_refused_ky_forms_records_them_on_the_server() = runTest {
        val queue = FakeOfflineSaleQueue(listOf(entry("p1", PendingSaleState.KyPending, listOf(ky10()))))
        val sales = FakeSaleRepository()
        pendingSalesOf(queue, sales).abandon("p1", "recorded on paper").getOrThrow()

        assertEquals(listOf(Triple("crid-p1", "ky_forms", "recorded on paper")), sales.abandoned)
        assertTrue(queue.entries.value.isEmpty())
    }

    @Test
    fun a_damaged_entry_is_exported_raw_and_only_it_can_be_discarded() = runTest {
        val damaged = entry("bad", PendingSaleState.Damaged).copy(payloadJson = "{not json")
        val queue = FakeOfflineSaleQueue(listOf(damaged, entry("ok")))
        val files = FakeFileDownloader()
        val sales = FakeSaleRepository()
        val pending = pendingSalesOf(queue, sales, files = files)

        assertTrue(pending.abandon("bad", "r").isFailure)
        assertEquals("/downloads/pending-sale-bad.json", pending.export("bad").getOrThrow())
        assertEquals("{not json", files.saved.single().second)

        assertTrue(pending.discardDamaged("ok").isFailure)
        pending.discardDamaged("bad").getOrThrow()
        assertEquals(listOf("ok"), queue.entries.value.map { it.id })
        assertTrue(sales.abandoned.isEmpty())
    }
}
