package app.devper.pharm.ui.i18n.groups

interface OfflineSyncStrings {
    val offlineSyncSubtitle: String
    val offlineSyncRetryAllCta: String
    val offlineSyncEmptyTitle: String
    val offlineSyncEmpty: String
    val offlineSyncMetricsTotal: String
    val offlineSyncMetricsLocation: String
    val offlineSyncMetricsFailed: String
    val offlineSyncStatusFailed: String
    val offlineSyncStatusPending: String
    val offlineSyncStatusSyncing: String
    val offlineSyncStatusRetry: String
    val offlineSyncAttemptsLabel: (Int) -> String
    val offlineSyncRetryRowCta: String
    val offlineSyncDeleteConfirmTitle: String
    val offlineSyncDeleteConfirmMessage: String
    val offlineSyncLoadFailed: String
    val offlineSyncSyncPartialFailed: (Int, Int) -> String
    val offlineSyncRetryFailed: (String) -> String
    val offlineSyncDiscardFailed: String
    val offlineSyncSyncStarted: (Int) -> String
    val offlineSyncRetryStarted: (String) -> String
    val offlineSyncDiscarded: String
    val offlineSyncStatusConflict: String
    val offlineSyncStatusKyPending: String
    val offlineSyncStatusDamaged: String
    val offlineSyncMetricsNeedsAction: String
    val offlineSyncNeedsActionSub: String
    val offlineSyncKyPendingBill: (String) -> String
    val offlineSyncDamagedHint: String
    val offlineSyncAbandonCta: String
    val offlineSyncCloseKyCta: String
    val offlineSyncExportCta: String
    val offlineSyncAbandonTitle: String
    val offlineSyncAbandonMessage: String
    val offlineSyncCloseKyTitle: String
    val offlineSyncCloseKyMessage: String
    val offlineSyncReasonPlaceholder: String
    val offlineSyncDiscardDamagedMessage: String
    val offlineSyncAbandonFailed: String
    val offlineSyncExportFailed: String
    val offlineSyncAbandoned: String
    val offlineSyncExported: (String) -> String
    val offlineSyncRecorded: String
    val offlineSyncStillNotRecorded: (String) -> String
}
