package app.devper.pharm.domain.pendingsales

import app.devper.pharm.common.ConflictException
import app.devper.pharm.common.NotFoundException
import app.devper.pharm.common.ServerException
import app.devper.pharm.common.ValidationException
import app.devper.pharm.common.platform.FileDownloader
import app.devper.pharm.common.platform.MimeType
import app.devper.pharm.domain.extension.isTemporaryDeliveryFailure
import app.devper.pharm.domain.extension.newClientRequestId
import app.devper.pharm.domain.model.AbandonOutcome
import app.devper.pharm.domain.model.KyForm
import app.devper.pharm.domain.model.PendingSale
import app.devper.pharm.domain.model.PendingSaleState
import app.devper.pharm.domain.repository.ky.KyRepository
import app.devper.pharm.domain.repository.offlinesync.OfflineSaleQueue
import app.devper.pharm.domain.repository.sales.SaleRepository
import app.devper.pharm.domain.usecase.ky.submitForms
import app.devper.pharm.domain.usecase.ky.withSaleId
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
class PendingSales(
    private val queue: OfflineSaleQueue,
    private val sales: SaleRepository,
    private val ky: KyRepository,
    private val files: FileDownloader,
) {
    val entries: StateFlow<List<PendingSale>> get() = queue.entries

    private val delivering = Mutex()

    fun enqueue(clientRequestId: String, payloadJson: String, kyForms: List<KyForm> = emptyList()): String {
        val id = "off-${newClientRequestId()}"
        queue.put(
            PendingSale(
                id = id,
                clientRequestId = clientRequestId,
                payloadJson = payloadJson,
                enqueuedAt = Clock.System.now().toEpochMilliseconds(),
                kyForms = kyForms,
            ),
        )
        return id
    }

    suspend fun syncAll(): SyncSummary = delivering.withLock {
        var summary = SyncSummary()
        for (entry in entries.value.filter { it.state == PendingSaleState.Pending }) {
            val outcome = deliver(entry)
            summary = summary.plus(outcome)
            if (outcome == Delivery.StillPending) break
        }
        summary
    }

    suspend fun retry(id: String): Result<PendingSaleState?> = attempt {
        delivering.withLock {
            val entry = find(id)
            if (entry.state == PendingSaleState.Damaged) throw ValidationException("damaged entry cannot be sent")
            deliver(entry)
            entries.value.firstOrNull { it.id == id }?.state
        }
    }

    suspend fun abandon(id: String, reason: String): Result<Unit> = attempt {
        if (reason.isBlank()) throw ValidationException("reason is required")
        delivering.withLock {
            val entry = find(id)
            when (entry.state) {
                PendingSaleState.Damaged -> throw ValidationException("damaged entry cannot be abandoned")
                PendingSaleState.KyPending -> {
                    sales.abandonKyForms(entry.clientRequestId, entry.kyForms, reason.trim())
                    queue.remove(id)
                }
                PendingSaleState.Pending, PendingSaleState.Conflict ->
                    when (val outcome = sales.abandonSale(entry.clientRequestId, entry.payloadJson, reason.trim())) {
                        AbandonOutcome.Abandoned -> queue.remove(id)
                        is AbandonOutcome.AlreadyRecorded ->
                            if (entry.kyForms.isEmpty()) {
                                queue.remove(id)
                            } else {
                                queue.put(
                                    entry.copy(
                                        state = PendingSaleState.Pending,
                                        billNo = outcome.sale.billNo,
                                        kyForms = entry.kyForms.map { it.withSaleId(outcome.sale.id) },
                                        lastError = null,
                                    ),
                                )
                            }
                    }
            }
        }
    }

    suspend fun export(id: String): Result<String> = attempt {
        val entry = find(id)
        files.save("pending-sale-${entry.id}.json", MimeType.Json, entry.payloadJson.encodeToByteArray()).getOrThrow()
    }

    fun discardDamaged(id: String): Result<Unit> {
        val entry = entries.value.firstOrNull { it.id == id }
            ?: return Result.failure(NotFoundException("Pending sale not found"))
        if (entry.state != PendingSaleState.Damaged) {
            return Result.failure(ValidationException("only damaged entries can be discarded"))
        }
        queue.remove(id)
        return Result.success(Unit)
    }

    private fun find(id: String): PendingSale =
        entries.value.firstOrNull { it.id == id } ?: throw NotFoundException("Pending sale not found")

    private suspend fun deliver(entry: PendingSale): Delivery {
        val sale = try {
            sales.replayCheckout(entry.payloadJson)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            return failed(entry, e)
        }
        if (entry.kyForms.isEmpty()) {
            queue.remove(entry.id)
            return Delivery.Recorded
        }
        val sent = ky.submitForms(entry.kyForms.map { it.withSaleId(sale.id) })
        val recorded = entry.copy(billNo = sale.billNo, kyForms = sent.remaining, attempts = entry.attempts + 1)
        return when {
            sent.remaining.isEmpty() -> {
                queue.remove(entry.id)
                Delivery.Recorded
            }
            sent.outage != null -> {
                queue.put(recorded.copy(state = PendingSaleState.Pending, lastError = sent.outage.message))
                Delivery.StillPending
            }
            else -> {
                queue.put(recorded.copy(state = PendingSaleState.KyPending, lastError = sent.refusedLabels.joinToString("\n")))
                Delivery.Refused
            }
        }
    }

    private fun failed(entry: PendingSale, e: Exception): Delivery {
        val temporary = e.isTemporaryDeliveryFailure()
        queue.put(
            entry.copy(
                state = if (temporary) PendingSaleState.Pending else PendingSaleState.Conflict,
                lastError = e.serverReason(),
                attempts = entry.attempts + 1,
            ),
        )
        return if (temporary) Delivery.StillPending else Delivery.Refused
    }

    private inline fun <T> attempt(block: () -> T): Result<T> = try {
        Result.success(block())
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        Result.failure(e)
    }
}

data class SyncSummary(val recorded: Int = 0, val refused: Int = 0, val stillPending: Int = 0) {
    internal fun plus(d: Delivery) = when (d) {
        Delivery.Recorded -> copy(recorded = recorded + 1)
        Delivery.Refused -> copy(refused = refused + 1)
        Delivery.StillPending -> copy(stillPending = stillPending + 1)
    }
}

internal enum class Delivery { Recorded, Refused, StillPending }

internal fun Throwable.serverReason(): String {
    val body = when (this) {
        is ConflictException -> payload
        is ServerException -> body
        else -> null
    }
    val parsed = body?.let {
        try {
            ((Json.parseToJsonElement(it) as? JsonObject)?.get("error") as? JsonPrimitive)?.content
        } catch (_: Exception) {
            null
        }
    }
    return parsed ?: message.orEmpty()
}
