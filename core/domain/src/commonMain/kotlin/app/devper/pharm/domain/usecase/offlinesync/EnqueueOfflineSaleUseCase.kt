package app.devper.pharm.domain.usecase

import app.devper.pharm.domain.param.EnqueueOfflineSaleParam
import app.devper.pharm.domain.repository.OfflineSaleQueue

class EnqueueOfflineSaleUseCase(private val queue: OfflineSaleQueue) :
    BaseSyncUseCase<EnqueueOfflineSaleParam, String>() {
    override fun execute(param: EnqueueOfflineSaleParam): String = queue.enqueue(param)
    operator fun invoke(clientRequestId: String, payloadJson: String): Result<String> =
        invoke(EnqueueOfflineSaleParam(clientRequestId = clientRequestId, payloadJson = payloadJson))
}
