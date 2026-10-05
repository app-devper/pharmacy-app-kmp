package app.devper.pharm.domain.observer

import app.devper.pharm.common.Logger
import app.devper.pharm.common.platform.ConnectivityObserver
import app.devper.pharm.domain.pendingsales.PendingSales
import app.devper.pharm.domain.pendingsales.SyncSummary
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

/** Syncs pending sales whenever the device comes back online. */
class OfflineAutoSync(
    private val connectivity: ConnectivityObserver,
    private val pendingSales: PendingSales,
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
        val summary = pendingSales.syncAll()
        if (summary != SyncSummary()) logger.debug(TAG, "online — synced pending sales: $summary")
    }

    private companion object {
        const val TAG = "OfflineAutoSync"
    }
}
