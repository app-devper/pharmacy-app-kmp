package app.devper.pharm.presentation.offlinesync.message

sealed class OfflineSyncUiStateMessage {
    data object Refreshed : OfflineSyncUiStateMessage()
    data class SyncStarted(val count: Int) : OfflineSyncUiStateMessage()
    data class RetryStarted(val billId: String) : OfflineSyncUiStateMessage()
    data object Discarded : OfflineSyncUiStateMessage()
}
