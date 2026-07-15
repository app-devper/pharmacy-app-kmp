package app.devper.pharm.presentation.offlinesync

import app.devper.pharm.common.AppException
import app.devper.pharm.domain.model.PendingSale
import app.devper.pharm.presentation.offlinesync.exception.OfflineSyncUiStateError
import app.devper.pharm.presentation.offlinesync.message.OfflineSyncUiStateMessage
import app.devper.pharm.ui.common.BaseUiState
import kotlinx.datetime.TimeZone

data class OfflineSyncUiState(
    val tz: TimeZone = TimeZone.of("Asia/Bangkok"),
    val pending: List<PendingSale> = emptyList(),
    val syncingIds: Set<String> = emptySet(),
    val syncingAll: Boolean = false,
    val discarding: Boolean = false,
    val confirmDiscardId: String? = null,
    val messageState: OfflineSyncUiStateMessage? = null,
    val errorState: OfflineSyncUiStateError? = null,
) : BaseUiState {

    override val loading: Boolean get() = syncingAll
    override val domainError: OfflineSyncUiStateError? get() = errorState
    val totalCount: Int get() = pending.size
    val failedCount: Int get() = pending.count { it.lastError != null }
    val busy: Boolean get() = syncingAll || syncingIds.isNotEmpty() || discarding
}
