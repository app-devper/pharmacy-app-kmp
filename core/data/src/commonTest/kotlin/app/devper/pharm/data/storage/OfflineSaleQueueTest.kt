package app.devper.pharm.data.storage

import app.devper.pharm.domain.param.EnqueueOfflineSaleParam
import app.devper.pharm.domain.param.MarkOfflineSaleFailedParam
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class OfflineSaleQueueTest {

    @Test
    fun enqueue_returns_unique_ids_and_preserves_insertion_order() {
        val queue = OfflineSaleQueueImpl(memorySettings())
        val first = queue.enqueue(EnqueueOfflineSaleParam(clientRequestId = "req-1", payloadJson = "{\"a\":1}"))
        val second = queue.enqueue(EnqueueOfflineSaleParam(clientRequestId = "req-2", payloadJson = "{\"b\":2}"))
        val pending = queue.pending.value
        assertEquals(2, pending.size)
        assertEquals(first, pending[0].id)
        assertEquals(second, pending[1].id)
        assertTrue(first != second)
    }

    @Test
    fun mark_synced_removes_entry() {
        val queue = OfflineSaleQueueImpl(memorySettings())
        val a = queue.enqueue(EnqueueOfflineSaleParam(clientRequestId = "r1", payloadJson = "{}"))
        val b = queue.enqueue(EnqueueOfflineSaleParam(clientRequestId = "r2", payloadJson = "{}"))
        queue.markSynced(a)
        val pending = queue.pending.value
        assertEquals(1, pending.size)
        assertEquals(b, pending[0].id)
    }

    @Test
    fun mark_failed_increments_attempts_and_stores_error() {
        val queue = OfflineSaleQueueImpl(memorySettings())
        val id = queue.enqueue(EnqueueOfflineSaleParam(clientRequestId = "r1", payloadJson = "{}"))
        queue.markFailed(MarkOfflineSaleFailedParam(id = id, error = "503"))
        queue.markFailed(MarkOfflineSaleFailedParam(id = id, error = "timeout"))
        val entry = queue.pending.value.single()
        assertEquals(2, entry.attempts)
        assertEquals("timeout", entry.lastError)
    }

    @Test
    fun clear_empties_queue_and_persists_to_settings() {
        val settings = memorySettings()
        val queue = OfflineSaleQueueImpl(settings)
        queue.enqueue(EnqueueOfflineSaleParam(clientRequestId = "r1", payloadJson = "{}"))
        queue.clear()
        assertTrue(queue.pending.value.isEmpty())
        assertNull(settings.getStringOrNull("offline.queue"))
    }

    @Test
    fun queue_survives_via_settings_when_reconstructed() {
        val settings = memorySettings()
        val first = OfflineSaleQueueImpl(settings)
        first.enqueue(EnqueueOfflineSaleParam(clientRequestId = "r1", payloadJson = "{\"x\":1}"))
        first.enqueue(EnqueueOfflineSaleParam(clientRequestId = "r2", payloadJson = "{\"y\":2}"))

        val reborn = OfflineSaleQueueImpl(settings)
        val pending = reborn.pending.value
        assertEquals(2, pending.size)
        assertEquals("r1", pending[0].clientRequestId)
        assertEquals("r2", pending[1].clientRequestId)
    }
}
