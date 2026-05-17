package app.devper.pharm.presentation.offlinesync

import app.devper.pharm.domain.model.PendingSale

data class OfflineSyncCallbacks(
    val onRefresh: () -> Unit = {},
    val onSyncAll: () -> Unit = {},
    val onRetry: (PendingSale) -> Unit = {},
    val onCancel: (PendingSale) -> Unit = {},
    val onConfirmCancel: () -> Unit = {},
    val onDismissCancel: () -> Unit = {},
    val onDismissMessage: () -> Unit = {},
    val onDismissError: () -> Unit = {},
)
