package app.devper.pharm.domain.usecase

import app.devper.pharm.domain.repository.OfflineSaleQueue

class MarkOfflineSaleSyncedUseCase(private val queue: OfflineSaleQueue) :
    BaseSyncUseCase<String, Unit>() {
    override fun execute(param: String) = queue.markSynced(param)
}
