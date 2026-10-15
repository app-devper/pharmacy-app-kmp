package app.devper.pharm.data.storage

import app.devper.pharm.domain.model.KyForm
import app.devper.pharm.domain.model.PendingSale
import app.devper.pharm.domain.model.PendingSaleState
import app.devper.pharm.domain.model.PendingSaleRefusal
import app.devper.pharm.domain.model.KyRefusal
import app.devper.pharm.domain.model.KyFormType
import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class OfflineSaleQueueTest {

    private fun sale(id: String, at: Long, state: PendingSaleState = PendingSaleState.Pending) = PendingSale(
        id = id, clientRequestId = "req-$id", payloadJson = """{"id":"$id"}""", enqueuedAt = at, state = state,
    )

    private val ky11 = KyForm.Ky11(
        saleId = "s1", date = LocalDate(2026, 9, 27), drugName = "Dextro", regNo = "R2", qty = 1,
        unit = "tab", buyerName = "A", purpose = "cough", pharmacist = "P",
    )

    @Test
    fun entries_survive_reconstruction_in_enqueue_order() {
        val settings = memorySettings()
        val queue = OfflineSaleQueueImpl(settings)
        queue.put(sale("b", 2))
        queue.put(sale("a", 1))

        val reborn = OfflineSaleQueueImpl(settings)
        assertEquals(listOf("a", "b"), reborn.entries.value.map { it.id })
        assertEquals("req-a", reborn.entries.value.first().clientRequestId)
    }

    @Test
    fun put_replaces_and_remove_deletes_one_entry() {
        val queue = OfflineSaleQueueImpl(memorySettings())
        queue.put(sale("a", 1))
        queue.put(sale("b", 2))
        queue.put(sale("a", 1).copy(attempts = 2, refusal = PendingSaleRefusal.Outage("timeout")))
        queue.remove("b")

        val only = queue.entries.value.single()
        assertEquals(2, only.attempts)
        assertEquals(PendingSaleRefusal.Outage("timeout"), only.refusal)
    }

    @Test
    fun a_refused_ky_form_round_trips_with_its_form_drug_and_reason() {
        val settings = memorySettings()
        val refusal = PendingSaleRefusal.KyRefused(listOf(KyRefusal(KyFormType.Ky11, "Codeine", "buyer_name is required")))
        OfflineSaleQueueImpl(settings).put(sale("a", 1, PendingSaleState.KyPending).copy(refusal = refusal))
        assertEquals(refusal, OfflineSaleQueueImpl(settings).entries.value.single().refusal)
    }

    @Test
    fun an_entry_saved_with_a_text_error_reads_back_as_a_typed_refusal() {
        val settings = memorySettings()
        settings.putString(
            "offline.sale.old-ky",
            """{"id":"old-ky","client_request_id":"r","payload":"{}","enqueued_at":1,"state":"ky_pending","last_error":"ky10:Tramadol:buyer_name is required\nky12:Morphine:rx_no is required"}""",
        )
        settings.putString(
            "offline.sale.old-conflict",
            """{"id":"old-conflict","client_request_id":"r2","payload":"{}","enqueued_at":2,"state":"conflict","last_error":"received must be >= total"}""",
        )
        val entries = OfflineSaleQueueImpl(settings).entries.value.associateBy { it.id }
        assertEquals(
            PendingSaleRefusal.KyRefused(
                listOf(
                    KyRefusal(KyFormType.Ky10, "Tramadol", "buyer_name is required"),
                    KyRefusal(KyFormType.Ky12, "Morphine", "rx_no is required"),
                ),
            ),
            entries.getValue("old-ky").refusal,
        )
        assertEquals(PendingSaleRefusal.Server("received must be >= total"), entries.getValue("old-conflict").refusal)
    }

    @Test
    fun state_bill_and_ky_forms_round_trip() {
        val settings = memorySettings()
        OfflineSaleQueueImpl(settings).put(
            sale("a", 1, PendingSaleState.KyPending).copy(billNo = "B1", kyForms = listOf(ky11)),
        )
        val e = OfflineSaleQueueImpl(settings).entries.value.single()
        assertEquals(PendingSaleState.KyPending, e.state)
        assertEquals("B1", e.billNo)
        assertEquals(listOf(ky11), e.kyForms)
    }

    @Test
    fun an_unreadable_entry_is_damaged_and_keeps_its_raw_data_without_losing_the_others() {
        val settings = memorySettings()
        OfflineSaleQueueImpl(settings).put(sale("good", 5))
        settings.putString("offline.sale.bad", "{truncated")

        val entries = OfflineSaleQueueImpl(settings).entries.value
        val damaged = entries.single { it.id == "bad" }
        assertEquals(PendingSaleState.Damaged, damaged.state)
        assertEquals("{truncated", damaged.payloadJson)
        assertEquals(PendingSaleState.Pending, entries.single { it.id == "good" }.state)
        assertEquals("{truncated", settings.getStringOrNull("offline.sale.bad"))
    }

    @Test
    fun the_old_single_list_queue_moves_to_one_entry_each() {
        val settings = memorySettings()
        settings.putString(
            "offline.queue",
            """[{"id":"a","client_request_id":"r1","payload":"{}","enqueued_at":1,"attempts":2},""" +
                """{"id":"b","client_request_id":"r2","payload":"{}","enqueued_at":2}]""",
        )
        val entries = OfflineSaleQueueImpl(settings).entries.value

        assertEquals(listOf("a", "b"), entries.map { it.id })
        assertTrue(entries.all { it.state == PendingSaleState.Pending })
        assertEquals(2, entries.first().attempts)
        assertNull(settings.getStringOrNull("offline.queue"))
    }

    @Test
    fun an_unreadable_old_queue_is_kept_as_one_damaged_entry() {
        val settings = memorySettings()
        settings.putString("offline.queue", "[{\"id\":")

        val entry = OfflineSaleQueueImpl(settings).entries.value.single()

        assertEquals(PendingSaleState.Damaged, entry.state)
        assertEquals("[{\"id\":", entry.payloadJson)
        assertNull(settings.getStringOrNull("offline.queue"))
    }

    @Test
    fun a_damaged_entry_stays_raw_when_put_back() {
        val settings = memorySettings()
        settings.putString("offline.sale.bad", "{x")
        val queue = OfflineSaleQueueImpl(settings)
        queue.put(queue.entries.value.single())
        assertEquals("{x", settings.getStringOrNull("offline.sale.bad"))
    }
}
