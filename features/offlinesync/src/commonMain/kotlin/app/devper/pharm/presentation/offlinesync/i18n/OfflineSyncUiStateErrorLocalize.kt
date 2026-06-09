package app.devper.pharm.presentation.offlinesync.i18n

import app.devper.pharm.presentation.offlinesync.exception.OfflineSyncUiStateError
import app.devper.pharm.ui.i18n.PharmStrings

fun OfflineSyncUiStateError.localize(s: PharmStrings): String = when (this) {
    is OfflineSyncUiStateError.LoadFailed -> s.offlineSyncLoadFailed
    is OfflineSyncUiStateError.SyncPartialFailed -> s.offlineSyncSyncPartialFailed(failed, total)
    is OfflineSyncUiStateError.RetryFailed -> s.offlineSyncRetryFailed(billId)
    is OfflineSyncUiStateError.DiscardFailed -> s.offlineSyncDiscardFailed
}
