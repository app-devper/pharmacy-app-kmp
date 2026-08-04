package app.devper.pharm.ui.i18n.groups

object OfflineSyncStringsEn : OfflineSyncStrings {
    override val offlineSyncSubtitle = "Offline bills not yet sent to the backend"
    override val offlineSyncRetryAllCta = "Sync all"
    override val offlineSyncEmptyTitle = "No pending sync items"
    override val offlineSyncEmpty = "All bills are synced with the backend"
    override val offlineSyncMetricsTotal = "Pending total"
    override val offlineSyncMetricsLocation = "On this device"
    override val offlineSyncMetricsFailed = "Sync failed"
    override val offlineSyncStatusFailed = "Failed"
    override val offlineSyncStatusPending = "Pending sync"
    override val offlineSyncStatusSyncing = "Syncing…"
    override val offlineSyncStatusRetry = "Awaiting retry"
    override val offlineSyncAttemptsLabel: (Int) -> String = { attempts -> "$attempts attempt(s)" }
    override val offlineSyncRetryRowCta = "Retry"
    override val offlineSyncDeleteConfirmTitle = "Delete pending sync item?"
    override val offlineSyncDeleteConfirmMessage =

        "This bill will be removed from the device queue — only do this if " +
        "the backend has already received it or you don't want to retry."
    override val offlineSyncLoadFailed = "Failed to load pending sync items"
    override val offlineSyncSyncPartialFailed: (Int, Int) -> String = { failed, total -> "$failed of $total bills failed to send" }
    override val offlineSyncRetryFailed: (String) -> String = { billId -> "Failed to send bill $billId" }
    override val offlineSyncDiscardFailed = "Failed to remove item"
    override val offlineSyncSyncStarted: (Int) -> String = { count -> "Syncing $count item(s)" }
    override val offlineSyncRetryStarted: (String) -> String = { billId -> "Retrying bill $billId" }
    override val offlineSyncDiscarded = "Pending sync item removed"
}
