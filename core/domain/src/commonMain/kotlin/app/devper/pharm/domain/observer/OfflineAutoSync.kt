package app.devper.pharm.domain.observer

import app.devper.pharm.common.Logger
import app.devper.pharm.common.platform.ConnectivityObserver
import app.devper.pharm.domain.repository.OfflineSaleQueue
import app.devper.pharm.domain.usecase.RetryOfflineSaleUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class OfflineAutoSync(
    private val connectivity: ConnectivityObserver,
    private val queue: OfflineSaleQueue,
    private val retry: RetryOfflineSaleUseCase,
    private val logger: Logger,
) {
    fun start(scope: CoroutineScope) {
        connectivity.online
            .distinctUntilChanged()
            .filter { it }
            .onEach { syncPending() }
            .launchIn(scope)
    }

    suspend fun syncPending() {
        val pending = queue.pending.value
        if (pending.isEmpty()) return
        logger.debug(TAG, "online — syncing ${pending.size} pending sale(s)")
        for (sale in pending) {
            retry(sale.id)
        }
    }

    private companion object {
        const val TAG = "OfflineAutoSync"
    }
}
