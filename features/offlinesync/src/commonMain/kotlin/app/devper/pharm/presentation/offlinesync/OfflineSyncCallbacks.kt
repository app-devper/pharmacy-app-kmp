package app.devper.pharm.presentation.offlinesync

import app.devper.pharm.domain.model.PendingSale

data class OfflineSyncCallbacks(
    val onSyncAll: () -> Unit = {},
    val onRetry: (PendingSale) -> Unit = {},
    val onAbandon: (PendingSale) -> Unit = {},
    val onExport: (PendingSale) -> Unit = {},
    val onDiscard: (PendingSale) -> Unit = {},
    val onReasonChange: (String) -> Unit = {},
    val onConfirmResolving: () -> Unit = {},
    val onDismissResolving: () -> Unit = {},
    val onDismissError: () -> Unit = {},
)
