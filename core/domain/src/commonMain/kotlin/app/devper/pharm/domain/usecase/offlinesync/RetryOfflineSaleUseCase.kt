package app.devper.pharm.domain.usecase.offlinesync

import app.devper.pharm.domain.usecase.BaseUseCase

import app.devper.pharm.common.AppDispatchers
import app.devper.pharm.common.NotFoundException
import app.devper.pharm.domain.model.Sale
import app.devper.pharm.domain.param.offlinesync.MarkOfflineSaleFailedParam
import app.devper.pharm.domain.repository.ky.KyRepository
import app.devper.pharm.domain.repository.offlinesync.OfflineSaleQueue
import app.devper.pharm.domain.repository.sales.SaleRepository
import app.devper.pharm.domain.usecase.ky.submitForms
import app.devper.pharm.domain.usecase.ky.withSaleId

/**
 * Replay a queued bill, then send the KY forms queued with it using the
 * confirmed sale id. Forms not yet recorded stay on the queued bill; replaying
 * it again returns the same sale because of its client request id.
 */
class RetryOfflineSaleUseCase(
    private val queue: OfflineSaleQueue,
    private val sales: SaleRepository,
    private val ky: KyRepository,
    dispatchers: AppDispatchers,
) : BaseUseCase<String, Sale>(dispatchers) {

    override suspend fun execute(param: String): Sale {
        val pending = queue.pending.value.firstOrNull { it.id == param }
            ?: throw NotFoundException("Pending sale not found")
        return try {
            val sale = sales.replayCheckout(pending.payloadJson)
            if (pending.kyForms.isNotEmpty()) {
                val sent = ky.submitForms(pending.kyForms.map { it.withSaleId(sale.id) })
                if (sent.remaining.isNotEmpty()) {
                    queue.setKyForms(param, sent.remaining)
                    throw sent.outage ?: KyFormsNotRecordedException(sent.refusedLabels)
                }
            }
            queue.markSynced(param)
            sale
        } catch (e: Exception) {
            queue.markFailed(MarkOfflineSaleFailedParam(id = param, error = e.message ?: ""))
            throw e
        }
    }
}

/** The bill was recorded but the server refused some of its KY forms. */
class KyFormsNotRecordedException(val failures: List<String>) :
    Exception("Sale recorded but KY forms were refused: ${failures.joinToString()}")
