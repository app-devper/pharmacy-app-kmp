package app.devper.pharm.data.storage

import app.devper.pharm.domain.model.PendingSale
import app.devper.pharm.domain.model.PendingSaleRefusal
import app.devper.pharm.domain.model.PendingSaleState
import app.devper.pharm.domain.repository.offlinesync.OfflineSaleQueue
import com.russhwolf.settings.Settings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.SerializationException
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

private const val ENTRY_PREFIX = "offline.sale."

private const val LEGACY_KEY = "offline.queue"

@OptIn(ExperimentalTime::class)
class OfflineSaleQueueImpl(
    private val settings: Settings,
) : OfflineSaleQueue {

    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    private val _entries = MutableStateFlow(emptyList<PendingSale>())
    override val entries: StateFlow<List<PendingSale>> = _entries.asStateFlow()

    init {
        migrateLegacyList()
        _entries.value = loadAll()
    }

    override fun put(entry: PendingSale) {
        val stored = if (entry.state == PendingSaleState.Damaged) {
            entry.payloadJson
        } else {
            json.encodeToString(PendingSaleDto.serializer(), entry.toDto())
        }
        settings.putString(ENTRY_PREFIX + entry.id, stored)
        _entries.value = loadAll()
    }

    override fun remove(id: String) {
        settings.remove(ENTRY_PREFIX + id)
        _entries.value = loadAll()
    }

    private fun loadAll(): List<PendingSale> =
        settings.keys
            .filter { it.startsWith(ENTRY_PREFIX) }
            .mapNotNull { key ->
                val raw = settings.getStringOrNull(key) ?: return@mapNotNull null
                decode(key.removePrefix(ENTRY_PREFIX), raw)
            }
            .sortedWith(compareBy({ it.enqueuedAt }, { it.id }))

    private fun decode(id: String, raw: String): PendingSale = try {
        json.decodeFromString(PendingSaleDto.serializer(), raw).toDomain()
    } catch (e: SerializationException) {
        damaged(id, raw, e)
    } catch (e: IllegalArgumentException) {
        damaged(id, raw, e)
    }

    private fun damaged(id: String, raw: String, e: Exception) = PendingSale(
        id = id,
        clientRequestId = "",
        payloadJson = raw,
        enqueuedAt = 0L,
        refusal = PendingSaleRefusal.Unreadable(e.message.orEmpty()),
        state = PendingSaleState.Damaged,
    )

    private fun migrateLegacyList() {
        val raw = settings.getStringOrNull(LEGACY_KEY) ?: return
        val list = try {
            json.decodeFromString(ListSerializer(PendingSaleDto.serializer()), raw)
        } catch (_: SerializationException) {
            null
        } catch (_: IllegalArgumentException) {
            null
        }
        if (list == null) {
            settings.putString(ENTRY_PREFIX + "damaged-${Clock.System.now().toEpochMilliseconds()}", raw)
        } else {
            list.forEach { settings.putString(ENTRY_PREFIX + it.id, json.encodeToString(PendingSaleDto.serializer(), it)) }
        }
        settings.remove(LEGACY_KEY)
    }

    private fun PendingSale.toDto() = PendingSaleDto(
        id = id,
        clientRequestId = clientRequestId,
        payload = payloadJson,
        enqueuedAt = enqueuedAt,
        lastError = null,
        refusal = refusal?.toDto(),
        attempts = attempts,
        ky = kyForms.map { it.toPendingDto() },
        state = when (state) {
            PendingSaleState.Conflict -> "conflict"
            PendingSaleState.KyPending -> "ky_pending"
            else -> "pending"
        },
        billNo = billNo,
    )

    private fun PendingSaleDto.toDomain() = PendingSale(
        id = id,
        clientRequestId = clientRequestId,
        payloadJson = payload,
        enqueuedAt = enqueuedAt,
        refusal = refusal?.toDomain() ?: lastError?.takeIf { it.isNotBlank() }?.let { legacyRefusal(it, kyPending = state == "ky_pending") },
        attempts = attempts,
        kyForms = ky.mapNotNull { it.toDomainOrNull() },
        state = when (state) {
            "pending" -> PendingSaleState.Pending
            "ky_pending" -> PendingSaleState.KyPending
            else -> PendingSaleState.Conflict
        },
        billNo = billNo,
    )
}
