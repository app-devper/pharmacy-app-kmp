package app.devper.pharm.ui.i18n.groups

object OfflineSyncStringsEn : OfflineSyncStrings {
    override val offlineSyncSubtitle = "Offline bills not yet sent to the backend"
    override val offlineSyncRetryAllCta = "Sync all"
    override val offlineSyncEmptyTitle = "No pending sync items"
    override val offlineSyncEmpty = "All bills are synced with the backend"
    override val offlineSyncMetricsTotal = "Pending total"
    override val offlineSyncMetricsLocation = "In IndexedDB"
    override val offlineSyncMetricsAttempts = "Total attempts"
    override val offlineSyncMetricsAttemptsSuffix = "attempts"
    override val offlineSyncMetricsFailed = "Sync failed"
    override val offlineSyncStatusFailed = "Failed"
    override val offlineSyncStatusPending = "Pending sync"
    override val offlineSyncStatusRetry = "Awaiting retry"
    override val offlineSyncAttemptsLabel: (Int) -> String = { attempts -> "$attempts attempt(s)" }
    override val offlineSyncRetryRowCta = "Retry"
    override val offlineSyncDeleteConfirmTitle = "Delete pending sync item?"
    override val offlineSyncDeleteConfirmMessage =

        "This bill will be removed from the device queue — only do this if " +
        "the backend has already received it or you don't want to retry."
}
