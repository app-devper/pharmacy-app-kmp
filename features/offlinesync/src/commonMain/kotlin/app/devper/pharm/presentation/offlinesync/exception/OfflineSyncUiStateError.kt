package app.devper.pharm.presentation.offlinesync.exception

import app.devper.pharm.common.AppException

sealed class OfflineSyncUiStateError(message: String, cause: Throwable? = null) : AppException(message, cause) {
    class LoadFailed(cause: Throwable? = null) : OfflineSyncUiStateError("offlinesync.load_failed", cause)
    class SyncPartialFailed(val failed: Int, val total: Int) : OfflineSyncUiStateError("offlinesync.sync_partial_failed")
    class RetryFailed(val billId: String, cause: Throwable? = null) : OfflineSyncUiStateError("offlinesync.retry_failed", cause)
    class DiscardFailed(cause: Throwable? = null) : OfflineSyncUiStateError("offlinesync.discard_failed", cause)
}
