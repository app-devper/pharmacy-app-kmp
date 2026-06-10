package app.devper.pharm.domain.usecase

import app.devper.pharm.common.AppDispatchers
import app.devper.pharm.common.NotFoundException
import app.devper.pharm.domain.model.Sale
import app.devper.pharm.domain.param.MarkOfflineSaleFailedParam
import app.devper.pharm.domain.repository.OfflineSaleQueue
import app.devper.pharm.domain.repository.SaleRepository

class RetryOfflineSaleUseCase(
    private val queue: OfflineSaleQueue,
    private val sales: SaleRepository,
    dispatchers: AppDispatchers,
) : BaseUseCase<String, Sale>(dispatchers) {

    override suspend fun execute(param: String): Sale {
        val pending = queue.pending.value.firstOrNull { it.id == param }
            ?: throw NotFoundException("Pending sale not found")
        return try {
            val sale = sales.replayCheckout(pending.payloadJson)
            queue.markSynced(param)
            sale
        } catch (e: Throwable) {
            queue.markFailed(MarkOfflineSaleFailedParam(id = param, error = e.message ?: "ไม่ทราบสาเหตุ"))
            throw e
        }
    }
}
