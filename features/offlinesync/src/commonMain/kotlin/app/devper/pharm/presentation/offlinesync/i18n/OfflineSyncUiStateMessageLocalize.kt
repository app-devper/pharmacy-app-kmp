package app.devper.pharm.presentation.offlinesync.i18n

import app.devper.pharm.presentation.offlinesync.message.OfflineSyncUiStateMessage
import app.devper.pharm.ui.i18n.PharmStrings

fun OfflineSyncUiStateMessage.localize(s: PharmStrings): String = when (this) {
    is OfflineSyncUiStateMessage.Refreshed -> s.offlineSyncRefreshed
    is OfflineSyncUiStateMessage.SyncStarted -> s.offlineSyncSyncStarted(count)
    is OfflineSyncUiStateMessage.RetryStarted -> s.offlineSyncRetryStarted(billId)
    is OfflineSyncUiStateMessage.Discarded -> s.offlineSyncDiscarded
}
