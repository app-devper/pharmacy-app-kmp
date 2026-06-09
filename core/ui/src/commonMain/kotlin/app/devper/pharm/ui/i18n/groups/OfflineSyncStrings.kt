package app.devper.pharm.ui.i18n.groups

interface OfflineSyncStrings {
    val offlineSyncSubtitle: String
    val offlineSyncRetryAllCta: String
    val offlineSyncEmptyTitle: String
    val offlineSyncEmpty: String
    val offlineSyncMetricsTotal: String
    val offlineSyncMetricsLocation: String
    val offlineSyncMetricsAttempts: String
    val offlineSyncMetricsAttemptsSuffix: String
    val offlineSyncMetricsFailed: String
    val offlineSyncStatusFailed: String
    val offlineSyncStatusPending: String
    val offlineSyncStatusRetry: String
    val offlineSyncAttemptsLabel: (Int) -> String
    val offlineSyncRetryRowCta: String
    val offlineSyncDeleteConfirmTitle: String
    val offlineSyncDeleteConfirmMessage: String
}
