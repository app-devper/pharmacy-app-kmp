package app.devper.pharm.domain.usecase.offlinesync

import app.devper.pharm.domain.usecase.BaseUseCase

import app.devper.pharm.common.AppDispatchers
import app.devper.pharm.domain.repository.offlinesync.OfflineSaleQueue

class MarkOfflineSaleSyncedUseCase(
    private val queue: OfflineSaleQueue,
    dispatchers: AppDispatchers,
) : BaseUseCase<String, Unit>(dispatchers) {
    override suspend fun execute(param: String) = queue.markSynced(param)
}
