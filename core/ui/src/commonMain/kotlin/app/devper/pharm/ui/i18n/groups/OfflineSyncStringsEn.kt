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
    override val offlineSyncDeleteConfirmTitle = "Delete damaged data?"
    override val offlineSyncDeleteConfirmMessage =

        "This unreadable entry will be removed from the device — " +
        "export it to a file first."
    override val offlineSyncLoadFailed = "Failed to load pending sync items"
    override val offlineSyncSyncPartialFailed: (Int, Int) -> String = { failed, total -> "$failed of $total bills failed to send" }
    override val offlineSyncRetryFailed: (String) -> String = { billId -> "Failed to send bill $billId" }
    override val offlineSyncDiscardFailed = "Failed to remove item"
    override val offlineSyncSyncStarted: (Int) -> String = { count -> "Syncing $count item(s)" }
    override val offlineSyncRetryStarted: (String) -> String = { billId -> "Retrying bill $billId" }
    override val offlineSyncDiscarded = "Pending sync item removed"
    override val offlineSyncStatusConflict = "Refused"
    override val offlineSyncStatusKyPending = "KY pending"
    override val offlineSyncStatusDamaged = "Damaged"
    override val offlineSyncMetricsNeedsAction = "Needs action"
    override val offlineSyncNeedsActionSub = "Not retried automatically"
    override val offlineSyncKyPendingBill: (String) -> String = { billNo -> "Bill $billNo recorded, but its KY forms were refused" }
    override val offlineSyncDamagedHint = "This entry cannot be read. Export it to a file first."
    override val offlineSyncAbandonCta = "Abandon bill"
    override val offlineSyncCloseKyCta = "Close KY"
    override val offlineSyncExportCta = "Export"
    override val offlineSyncAbandonTitle = "Abandon pending bill?"
    override val offlineSyncAbandonMessage =
        "This bill will not be recorded as a sale. The bill and your reason are kept for audit."
    override val offlineSyncCloseKyTitle = "Close pending KY forms?"
    override val offlineSyncCloseKyMessage =
        "The refused KY forms will not be recorded. The forms and your reason are kept for audit."
    override val offlineSyncReasonPlaceholder = "Reason (required)"
    override val offlineSyncDiscardDamagedMessage = "Damaged data removed"
    override val offlineSyncAbandonFailed = "Could not abandon"
    override val offlineSyncExportFailed = "Could not export"
    override val offlineSyncAbandoned = "Abandonment recorded"
    override val offlineSyncExported: (String) -> String = { path -> "Saved: $path" }
    override val offlineSyncRecorded = "Bill recorded"
    override val offlineSyncStillNotRecorded: (String) -> String = { billId -> "Bill $billId is still not recorded; see the reason on the item" }
}
