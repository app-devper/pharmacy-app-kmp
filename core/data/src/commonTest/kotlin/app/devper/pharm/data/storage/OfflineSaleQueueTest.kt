package app.devper.pharm.data.storage

import app.devper.pharm.domain.param.EnqueueOfflineSaleParam
import app.devper.pharm.domain.param.MarkOfflineSaleFailedParam
import com.russhwolf.settings.Settings
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

internal fun memorySettings(): Settings = MemorySettings()

internal class MemorySettings : Settings {
    private val store = mutableMapOf<String, Any?>()
    override val keys: Set<String> get() = store.keys
    override val size: Int get() = store.size
    override fun clear() = store.clear()
    override fun remove(key: String) { store.remove(key) }
    override fun hasKey(key: String): Boolean = store.containsKey(key)
    override fun putInt(key: String, value: Int) { store[key] = value }
    override fun getInt(key: String, defaultValue: Int): Int = (store[key] as? Int) ?: defaultValue
    override fun getIntOrNull(key: String): Int? = store[key] as? Int
    override fun putLong(key: String, value: Long) { store[key] = value }
    override fun getLong(key: String, defaultValue: Long): Long = (store[key] as? Long) ?: defaultValue
    override fun getLongOrNull(key: String): Long? = store[key] as? Long
    override fun putString(key: String, value: String) { store[key] = value }
    override fun getString(key: String, defaultValue: String): String = (store[key] as? String) ?: defaultValue
    override fun getStringOrNull(key: String): String? = store[key] as? String
    override fun putFloat(key: String, value: Float) { store[key] = value }
    override fun getFloat(key: String, defaultValue: Float): Float = (store[key] as? Float) ?: defaultValue
    override fun getFloatOrNull(key: String): Float? = store[key] as? Float
    override fun putDouble(key: String, value: Double) { store[key] = value }
    override fun getDouble(key: String, defaultValue: Double): Double = (store[key] as? Double) ?: defaultValue
    override fun getDoubleOrNull(key: String): Double? = store[key] as? Double
    override fun putBoolean(key: String, value: Boolean) { store[key] = value }
    override fun getBoolean(key: String, defaultValue: Boolean): Boolean = (store[key] as? Boolean) ?: defaultValue
    override fun getBooleanOrNull(key: String): Boolean? = store[key] as? Boolean
}


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
