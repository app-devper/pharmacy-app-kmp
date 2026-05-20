package app.devper.pharm.data.storage

import app.devper.pharm.domain.model.StockCountDraft
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class StockCountDraftStorageTest {

    @Test
    fun load_returns_empty_when_nothing_persisted() {
        val storage = StockCountDraftStorage(memorySettings())
        val draft = storage.load()
        assertTrue(draft.isEmpty)
    }

    @Test
    fun save_then_load_round_trips_counts_and_note() {
        val storage = StockCountDraftStorage(memorySettings())
        val draft = StockCountDraft(
            counts = mapOf("d1" to "5", "d2" to "12"),
            note = "Cycle May",
            updatedAt = 1_715_000_000_000L,
        )
        storage.save(draft)
        val loaded = storage.load()
        assertEquals(draft.counts, loaded.counts)
        assertEquals(draft.note, loaded.note)
        assertEquals(draft.updatedAt, loaded.updatedAt)
    }

    @Test
    fun save_with_empty_draft_removes_persisted_entry() {
        val settings = memorySettings()
        val storage = StockCountDraftStorage(settings)
        storage.save(StockCountDraft(counts = mapOf("d1" to "5"), note = "n", updatedAt = 1L))
        storage.save(StockCountDraft.Empty)
        assertTrue(storage.load().isEmpty)
        assertEquals(0, settings.size)
    }

    @Test
    fun clear_removes_persisted_entry() {
        val settings = memorySettings()
        val storage = StockCountDraftStorage(settings)
        storage.save(StockCountDraft(counts = mapOf("d1" to "5"), note = "n", updatedAt = 1L))
        storage.clear()
        assertEquals(0, settings.size)
    }

    @Test
    fun malformed_payload_is_discarded_and_load_returns_empty() {
        val settings = memorySettings()
        settings.putString("stockcount.draft", "not-json")
        val storage = StockCountDraftStorage(settings)
        val loaded = storage.load()
        assertTrue(loaded.isEmpty)
        assertEquals(0, settings.size)
    }
}
