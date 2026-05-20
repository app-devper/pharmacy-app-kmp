package app.devper.pharm.data.storage

import app.devper.pharm.domain.model.StockCountDraft
import app.devper.pharm.domain.repository.StockCountDraftRepository
import com.russhwolf.settings.Settings
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json

class StockCountDraftStorage(private val settings: Settings) : StockCountDraftRepository {

    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    override fun load(): StockCountDraft {
        val raw = settings.getStringOrNull(KEY) ?: return StockCountDraft.Empty
        return try {
            val dto = json.decodeFromString(StockCountDraftDto.serializer(), raw)
            if (dto.version != StockCountDraftDto.SCHEMA_VERSION) {
                settings.remove(KEY)
                StockCountDraft.Empty
            } else {
                StockCountDraft(
                    counts = dto.counts,
                    note = dto.note,
                    updatedAt = dto.updatedAt,
                )
            }
        } catch (_: SerializationException) {
            settings.remove(KEY)
            StockCountDraft.Empty
        }
    }

    override fun save(draft: StockCountDraft) {
        if (draft.isEmpty) {
            settings.remove(KEY)
            return
        }
        val dto = StockCountDraftDto(
            counts = draft.counts,
            note = draft.note,
            updatedAt = draft.updatedAt,
        )
        val raw = json.encodeToString(StockCountDraftDto.serializer(), dto)
        settings.putString(KEY, raw)
    }

    override fun clear() {
        settings.remove(KEY)
    }

    companion object {
        private const val KEY = "stockcount.draft"
    }
}
