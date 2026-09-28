package app.devper.pharm.presentation.offlinesync.i18n

import app.devper.pharm.presentation.offlinesync.message.OfflineSyncUiStateMessage
import app.devper.pharm.ui.i18n.PharmStrings

fun OfflineSyncUiStateMessage.localize(s: PharmStrings): String = when (this) {
    is OfflineSyncUiStateMessage.SyncStarted -> s.offlineSyncSyncStarted(count)
    is OfflineSyncUiStateMessage.RetryStarted -> s.offlineSyncRetryStarted(billId)
    is OfflineSyncUiStateMessage.Recorded -> s.offlineSyncRecorded
    is OfflineSyncUiStateMessage.Abandoned -> s.offlineSyncAbandoned
    is OfflineSyncUiStateMessage.Exported -> s.offlineSyncExported(path)
    is OfflineSyncUiStateMessage.Discarded -> s.offlineSyncDiscardDamagedMessage
}
