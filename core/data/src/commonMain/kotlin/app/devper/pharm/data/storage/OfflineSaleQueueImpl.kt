package app.devper.pharm.data.storage

import app.devper.pharm.domain.model.PendingSale
import app.devper.pharm.domain.param.EnqueueOfflineSaleParam
import app.devper.pharm.domain.param.MarkOfflineSaleFailedParam
import app.devper.pharm.domain.repository.OfflineSaleQueue
import app.devper.pharm.domain.extension.newClientRequestId
import com.russhwolf.settings.Settings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.SerializationException
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

private const val KEY = "offline.queue"

@OptIn(ExperimentalTime::class)
class OfflineSaleQueueImpl(
    private val settings: Settings,
) : OfflineSaleQueue {

    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }
    private val listSerializer = ListSerializer(PendingSaleDto.serializer())

    private val _pending = MutableStateFlow(loadAll())
    override val pending: StateFlow<List<PendingSale>> = _pending.asStateFlow()

    override fun enqueue(param: EnqueueOfflineSaleParam): String {
        val id = "off-${newClientRequestId()}"
        val now = Clock.System.now().toEpochMilliseconds()
        val newEntry = PendingSaleDto(
            id = id,
            clientRequestId = param.clientRequestId,
            payload = param.payloadJson,
            enqueuedAt = now,
        )
        val current = readDtos()
        val next = current + newEntry
        writeDtos(next)
        _pending.value = next.map { it.toDomain() }
        return id
    }

    override fun markSynced(id: String) {
        val current = readDtos()
        val next = current.filterNot { it.id == id }
        if (next.size != current.size) {
            writeDtos(next)
            _pending.value = next.map { it.toDomain() }
        }
    }

    override fun markFailed(param: MarkOfflineSaleFailedParam) {
        val current = readDtos()
        if (current.none { it.id == param.id }) return
        val next = current.map {
            if (it.id == param.id) it.copy(lastError = param.error, attempts = it.attempts + 1) else it
        }
        writeDtos(next)
        _pending.value = next.map { it.toDomain() }
    }

    override fun clear() {
        settings.remove(KEY)
        _pending.value = emptyList()
    }

    private fun loadAll(): List<PendingSale> = readDtos().map { it.toDomain() }

    private fun readDtos(): List<PendingSaleDto> {
        val raw = settings.getStringOrNull(KEY) ?: return emptyList()
        return try {
            json.decodeFromString(listSerializer, raw)
        } catch (_: SerializationException) {

            settings.remove(KEY)
            emptyList()
        }
    }

    private fun writeDtos(items: List<PendingSaleDto>) {
        if (items.isEmpty()) {
            settings.remove(KEY)
        } else {
            settings.putString(KEY, json.encodeToString(listSerializer, items))
        }
    }

    private fun PendingSaleDto.toDomain() = PendingSale(
        id = id,
        clientRequestId = clientRequestId,
        payloadJson = payload,
        enqueuedAt = enqueuedAt,
        lastError = lastError,
        attempts = attempts,
    )
}
