package app.devper.pharm.domain.usecase

import app.devper.pharm.common.AppDispatchers
import app.devper.pharm.domain.repository.OfflineSaleQueue

class MarkOfflineSaleSyncedUseCase(
    private val queue: OfflineSaleQueue,
    dispatchers: AppDispatchers,
) : BaseUseCase<String, Unit>(dispatchers) {
    override suspend fun execute(param: String) = queue.markSynced(param)
}
