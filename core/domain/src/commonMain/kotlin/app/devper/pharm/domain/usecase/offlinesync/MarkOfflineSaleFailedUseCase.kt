package app.devper.pharm.domain.usecase

import app.devper.pharm.domain.param.MarkOfflineSaleFailedParam
import app.devper.pharm.domain.repository.OfflineSaleQueue

class MarkOfflineSaleFailedUseCase(private val queue: OfflineSaleQueue) :
    BaseSyncUseCase<MarkOfflineSaleFailedParam, Unit>() {
    override fun execute(param: MarkOfflineSaleFailedParam) = queue.markFailed(param)
    operator fun invoke(id: String, error: String): Result<Unit> =
        invoke(MarkOfflineSaleFailedParam(id = id, error = error))
}
